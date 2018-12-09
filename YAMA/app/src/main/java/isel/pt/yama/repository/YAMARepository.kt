package isel.pt.yama.repository

import android.graphics.Bitmap
import android.util.Log
import com.android.volley.VolleyError
import com.android.volley.toolbox.RequestFuture
import isel.pt.yama.kotlinx.AsyncWork
import isel.pt.yama.kotlinx.runAsync
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.repository.dataAccess.database.*
import isel.pt.yama.repository.dataAccess.github.GithubApi
import isel.pt.yama.repository.dataAccess.firebase.FirebaseDatabase
import isel.pt.yama.repository.dataAccess.mappers.Mappers
import isel.pt.yama.repository.dataAccess.dto.*
import isel.pt.yama.repository.dataAccess.github.IGithubApi
import isel.pt.yama.repository.model.*


// Holds all the data needed and interfaces with volley or any other data source.
class YAMARepository(private val app: YAMAApplication,
                     private val api: IGithubApi,
                     private val localDb: YAMADatabase,
                     private val firebase: FirebaseDatabase) {

    val mappers = Mappers(this)

    var token: String = ""
    var organizationID: String = ""
    var currentUser: User? = null
    var otherUser: User? = null
    var team: Team? = null
    val avatarCache : HashMap<String, Bitmap> = HashMap()
    val userAvatarUrlCache : HashMap<String, String> = HashMap()
    val TAG = YAMARepository::class.java.simpleName
    var msgIconResource: Int = R.mipmap.ic_msg_not_sent


    private fun saveToDB(orgId: String, teams: List<TeamDto>): AsyncWork<List<Team>> {
        return runAsync {
            syncSaveTeamsFromDTO(app, localDb, orgId, teams)
        }
    }
    private fun saveToDB(user: UserDto): AsyncWork<User> {
        return runAsync {
            syncSaveUserFromDTO(app, localDb, user)
        }
    }
    private fun saveToDB(organizations: List<OrganizationDto>): AsyncWork<List<Organization>> {
        return runAsync {
            syncSaveOrganizationsFromDTO(app, localDb, organizations)
        }
    }
    private fun saveToDBUser(user : String, organizations: List<OrganizationDto>): AsyncWork<List<Organization>> {
        return saveToDB(organizations).andThen {
            runAsync { syncSaveUserOrganizationsFromDTO(app, localDb, user, organizations) }}
    }
    private fun saveToDB(team: Int, organization: String, members: List<UserDto>): AsyncWork<List<User>> {
        return runAsync {
                    syncSaveTeamMemberFromDTO(app, localDb, team, organization, members)
                }
    }

    private fun syncSaveTeamsFromDTO(app: YAMAApplication, db: YAMADatabase, organization: String, teams: List<TeamDto>): List<Team> {
        Log.v(app.TAG, "syncSaveTeamsFromDTO: Saving teams to DB")

        //val result = dto.quotes.map { Quote(it.currency, it.quote, date) }
        val result = teams.map { dto ->
            TeamDB(dto.name, dto.id, organization, dto.description)
        }
        db.teamDAO().insertAll(*result.toTypedArray())//result.toTypedArray())

        return teams.map(mappers.teamMapper::dtoToModel)
    }
  

    private fun syncSaveTeamMemberFromDTO(
            app: YAMAApplication,
            db: YAMADatabase,
            team: Int,
            organization: String, members: List<UserDto>): List<User>
    {
        Log.v(app.TAG, "Saving team members to DB")

        members.forEach {
            if (db.userDAO().getUser(it.login) == null)
                db.userDAO().insertUser(
                        UserDB(it.login, it.id, it.name, it.email, it.avatar_url, it.followers, it.following)
                )
            db.teamMembersDAO().insert(TeamMemberDB(organization, team, it.login))
        }

        return db.teamMembersDAO().getTeamMembers(team, organization).map(mappers.userMapper::dbToModel)

    }
    private fun syncSaveOrganizationsFromDTO(app: YAMAApplication, db: YAMADatabase, organizations: List<OrganizationDto>): List<Organization> {
        Log.v(app.TAG, "syncSaveOrganizationsFromDTO: Saving organizations to DB")
        val result = organizations.map { dto ->
            OrganizationDB(dto.login, dto.id)
        }
        db.organizationDAO().insertAll(*result.toTypedArray())
        return result.map(mappers.organizationMapper::dbToModel)
    }
    private fun syncSaveUserFromDTO(app: YAMAApplication, db: YAMADatabase, user: UserDto): User {
        Log.v(app.TAG, "syncSaveUserFromDTO: Saving currentUser to DB")
        val result = mappers.userMapper.dtoToDb(user)
        db.userDAO().insertUsers(result)
        return mappers.userMapper.dtoToModel(user)
    }
    private fun syncSaveUserOrganizationsFromDTO(app: YAMAApplication, db: YAMADatabase, user: String, organizations: List<OrganizationDto>): List<Organization> {
        Log.v(TAG, "Saving currentUser organizations to DB")

        val data = organizations.map { dto ->
            OrganizationMemberDB(dto.login, user)
        }

        db.organizationMembersDAO().insertAll(*data.toTypedArray())

        return organizations.map(mappers.organizationMapper::dtoToModel)
    }

    fun getUserDetails(userLogin: String, accessToken : String, success: (User) -> Unit, fail: (VolleyError) -> Unit) {

        getUserAux(userLogin, success, fail){
            api.getUserDetails(accessToken, {
                userAvatarUrlCache[userLogin] = it.avatar_url
                saveToDB(it).andThen(success)
            }, fail)
        }

    }
    fun getUser(userLogin: String, success: (User) -> Unit, fail: (VolleyError) -> Unit) {

        getUserAux(userLogin, success, fail){
                api.getUserDetailsForName(userLogin, {
                    userAvatarUrlCache[userLogin] = it.avatar_url
                    saveToDB(it).andThen(success)
                }, fail)
        }
    }

    private fun getUserAux(userLogin: String, success: (User) -> Unit, fail: (VolleyError) -> Unit, getAndSaveToDb: () -> Unit) {
        runAsync {
            Log.v(TAG, "Getting currentUser from DB")

            localDb.userDAO().getUser(userLogin)
        }.andThen { user ->
            if (user == null)
                getAndSaveToDb()
            else {
                Log.v(app.TAG, "getUserDetails: Got currentUser from DB")
                userAvatarUrlCache[userLogin] = user.avatarUrl

                success(mappers.userMapper.dbToModel(user))
            }
        }
    }


    private fun getFreshUserInfo(user: String, success: (User) -> Unit, fail: (VolleyError) -> Unit){

        api.getUserDetailsForName(user, {
            userAvatarUrlCache[user] = it.avatar_url
            saveToDB(it).andThen{
                u -> success(u)
            }
        }, fail)

    }

    fun getUserOrganizations(user: String, accessToken : String, success: (List<Organization>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(TAG, "Getting organizations from DB")
            localDb.organizationMembersDAO().getUserOrganizations(user)
        }.andThen { organizations ->
            if (organizations.isEmpty())
                api.getUserOrganizations(accessToken, {
                     saveToDBUser(user, it).andThen(success)
            }, fail)
            else {
                Log.v(TAG, "Got organizations from DB")
                success(organizations.map(mappers.organizationMapper::dbToModel))
            }
        }
    }
    fun getTeams(success: (List<Team>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(TAG, "Getting teams from DB")
            localDb.teamDAO().getOrganizationTeams(organizationID)
        }.andThen { teams ->
            if (teams.isEmpty())// Then data isn't stored in localDb so fetch from API
                api.getTeams(organizationID, {
                    saveToDB(organizationID, it).andThen(success)
                }, fail)
            else {
                Log.v(TAG, "Got teams from DB")

                success(teams.map(mappers.teamMapper::dbToModel))
            }
        }
    }

    fun syncGetTeams(app: YAMAApplication, token: String, orgId: String): List<Team> {
        Log.v(app.TAG, "Sync getting teams from API")
        val future: RequestFuture<List<TeamDto>> = RequestFuture.newFuture()
        api.getTeamsWithToken(orgId, token, future, future)
        return syncSaveTeamsFromDTO(app, localDb, orgId, future.get())
    }

    fun getSubscribedTeams(success: (List<Team>) -> Unit, fail: (Exception) -> Unit) {
        firebase.getSubscribedTeams(currentUser!!, success, fail)
    }


    fun syncGetTeamMembers(app: YAMAApplication, token: String, teamId: Int) : List<User> {
        Log.v(app.TAG, "Sync getting team members from API")
        val future: RequestFuture<List<UserDto>> = RequestFuture.newFuture()
        api.getTeamMembersWithToken(teamId, token, future, future)
        return syncSaveTeamMemberFromDTO(app, localDb, teamId, organizationID, future.get())
    }

    fun getTeamMembers(team: Int, organization: String, success: (List<User>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(TAG, "Getting team members from DB")
            localDb.teamMembersDAO().getTeamMembers(team, organization)
        }.andThen { members ->
            if (members.isEmpty())
                api.getTeamMembers(team, {
                    saveToDB(team, organization, it).andThen(success)
                }, fail)
            else {
                Log.v(TAG, "Got organizations from DB")
                success(members.map(mappers.userMapper::dbToModel))
            }
        }
    }


    fun sendTeamMessage(team : Team, message: Message){
        firebase.sendMessage(mappers.messageMapper.modelToDto(message), team)
    }

    fun sendUserMessage(otherUserLogin: String, message: Message) {
        firebase.sendUserMessage(mappers.messageMapper.modelToDto(message), otherUserLogin)
    }

    fun updateCurrentUser( cb : (User)->Unit) {
        getFreshUserInfo(
            currentUser!!.login
            ,{
            currentUser=it
            cb(it)
            }
            ,{defaultErrorHandler(app, it)}
        )
    }



    fun updateOtherUser( cb : (User)->Unit) {
        getFreshUserInfo(
            otherUser!!.login
            ,{
                otherUser=it
                cb(it)
            }
            ,{defaultErrorHandler(app, it)}
        )
    }

    fun getSubscribedUserChats(success: (List<UserAssociation>) -> Unit, fail: (Exception) -> Unit) {
        firebase.getSubscribedUsers(currentUser!!, success, fail)
    }
}