package isel.pt.yama.dataAccess

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import isel.pt.yama.kotlinx.AsyncWork
import isel.pt.yama.kotlinx.runAsync
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dataAccess.database.*
import isel.pt.yama.dataAccess.github.GithubApi
import isel.pt.yama.dataAccess.firebase.FirebaseDatabase
import isel.pt.yama.dataAccess.mappers.Mappers
import isel.pt.yama.dto.*
import isel.pt.yama.model.*
import isel.pt.yama.network.GetRequestImage


// Holds all the data needed and interfaces with volley or any other data source.
class YAMARepository(private val app: YAMAApplication,
                     private val api: GithubApi,
                     private val localDb: YAMADatabase,
                     private val firebase: FirebaseDatabase) {

    val mappers = Mappers(this)

    var token: String = ""
    var organizationID: String = ""
    var currentUser: UserMD? = null
    var otherUser: UserMD? = null
    var team: TeamMD? = null
    val avatarCache : HashMap<String, Bitmap> = HashMap()
    val userAvatarUrlCache : HashMap<String, String> = HashMap()
    val TAG = YAMARepository::class.java.simpleName


    //TODO: implement this
    private fun saveToDB(orgId: String, teams: List<TeamDto>): AsyncWork<List<TeamMD>> {
        return runAsync {
            //TODO: what is success property???
            //if (dto.success) syncSaveTeamsFromDTO(app, teamDao, teams)
            //else listOf()
            syncSaveTeamsFromDTO(app, localDb, orgId, teams)
        }
    }
    private fun saveToDB(user: UserDto): AsyncWork<UserMD> {
        return runAsync {
            syncSaveUserFromDTO(app, localDb, user)
        }
    }
    private fun saveToDB(organizations: List<OrganizationDto>): AsyncWork<List<OrganizationMD>> {
        return runAsync {
            syncSaveOrganizationsFromDTO(app, localDb, organizations)
        }
    }

    private fun saveToDBUSer(user : String, organizations: List<OrganizationDto>): AsyncWork<List<OrganizationMD>> {
        return saveToDB(organizations).andThen {
            runAsync { syncSaveUserOrganizationsFromDTO(app, localDb, user, organizations) }}
    }
    private fun saveToDB(team: Int, organization: String, members: List<UserDto>): AsyncWork<List<UserMD>> {
        return runAsync {
                    syncSaveTeamMemberFromDTO(app, localDb, team, organization, members)
                }
    }



    private fun syncSaveTeamsFromDTO(app: YAMAApplication, db: YAMADatabase, organization: String, teams: List<TeamDto>): List<TeamMD> {
        Log.v(app.TAG, "syncSaveTeamsFromDTO: Saving teams to DB")

        //val result = dto.quotes.map { Quote(it.currency, it.quote, date) }
        val result = teams.map { dto ->
            Team(dto.name, dto.id, organization, dto.description)
        }
        db.teamDAO().insertAll(*result.toTypedArray())//result.toTypedArray())

        return teams.map(mappers.teamMapper::dtoToMD)
    }
  

    private fun syncSaveTeamMemberFromDTO(
            app: YAMAApplication,
            db: YAMADatabase,
            team: Int,
            organization: String, members: List<UserDto>): List<UserMD>
    {
        Log.v(app.TAG, "Saving team members to DB")

        members.forEach {
            if (db.userDAO().getUser(it.login) == null)
                db.userDAO().insertUser(
                        User(it.login, it.id, it.name, it.email, it.avatar_url, it.followers, it.following)
                )
            db.teamMembersDAO().insert(TeamMember(organization, team, it.login))
        }

        return db.teamMembersDAO().getTeamMembers(team, organization).map(mappers.userMapper::dbToMD)

    }
    private fun syncSaveOrganizationsFromDTO(app: YAMAApplication, db: YAMADatabase, organizations: List<OrganizationDto>): List<OrganizationMD> {
        Log.v(app.TAG, "syncSaveOrganizationsFromDTO: Saving organizations to DB")
        val result = organizations.map { dto ->
            Organization(dto.login, dto.id)
        }
        db.organizationDAO().insertAll(*result.toTypedArray())
        return result.map(mappers.organizationMapper::dbToMD)
    }
    private fun syncSaveUserFromDTO(app: YAMAApplication, db: YAMADatabase, user: UserDto): UserMD {
        Log.v(app.TAG, "syncSaveUserFromDTO: Saving currentUser to DB")
        val result = mappers.userMapper.dtoToDb(user)
        db.userDAO().insertUsers(result)
        return mappers.userMapper.dtoToMD(user)
    }

    private fun syncSaveUserOrganizationsFromDTO(app: YAMAApplication, db: YAMADatabase, user: String, organizations: List<OrganizationDto>): List<OrganizationMD> {
        Log.v(TAG, "Saving currentUser organizations to DB")

        val data = organizations.map { dto ->
            OrganizationMember(dto.login, user)
        }

        db.organizationMembersDAO().insertAll(*data.toTypedArray())

        return organizations.map(mappers.organizationMapper::dtoToMD)
    }

    fun getUserDetails(userLogin: String, accessToken : String, success: (UserMD) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(TAG, "Getting currentUser from DB")

            localDb.userDAO().getUser(userLogin)
        }.andThen { user ->
            if (user == null)
                api.getUserDetails(accessToken, {
                    userAvatarUrlCache[userLogin] = it.avatar_url
                    saveToDB(it).andThen(success)
                }, fail)
            else {
                Log.v(app.TAG, "getUserDetails: Got currentUser from DB")
                userAvatarUrlCache[userLogin] = user.avatarUrl

                success(mappers.userMapper.dbToMD(user))
            }
        }
    }

    fun getUser(userLogin: String, success: (UserMD) -> Unit, fail: (VolleyError) -> Unit) { //TODO codigo repetido

        runAsync {
            Log.v(TAG, "Getting currentUser from DB")

            localDb.userDAO().getUser(userLogin)
        }.andThen { user ->
            if (user == null)
                api.getUserDetailsForName(userLogin, {
                    userAvatarUrlCache[userLogin] = it.avatar_url
                    saveToDB(it).andThen(success)
                }, fail)
            else {
                Log.v(app.TAG, "getUserDetails: Got currentUser from DB")
                userAvatarUrlCache[userLogin] = user.avatarUrl

                success(mappers.userMapper.dbToMD(user))
            }
        }
    }



    fun getUserOrganizations(user: String, accessToken : String, success: (List<OrganizationMD>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(TAG, "Getting organizations from DB")
            localDb.organizationMembersDAO().getUserOrganizations(user)
        }.andThen { organizations ->
            if (organizations.isEmpty())
                api.getUserOrganizations(accessToken, {
                     saveToDBUSer(user, it).andThen(success)
            }, fail)
            else {
                Log.v(TAG, "Got organizations from DB")
                success(organizations.map(mappers.organizationMapper::dbToMD))
            }
        }
    }

    fun getTeams(success: (List<TeamMD>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(TAG, "Getting teams from DB")
            localDb.teamDAO().getOrganizationTeams(organizationID)
        }.andThen { teams ->
            if (teams.isEmpty())// Then data isn't stored in localDb so fetch from API
                api.getTeams(organizationID, {
                    //TODO: finish saving to localDb
                    saveToDB(organizationID, it).andThen(success)
                }, fail)
            else {
                Log.v(TAG, "Got teams from DB")

                success(teams.map(mappers.teamMapper::dbToMD))
            }
        }
    }

    fun getSubscribedTeams(success: (List<TeamMD>) -> Unit, fail: (Exception) -> Unit) {
        firebase.getSubscribedTeams(currentUser!!, success, fail)
    }

    fun getTeamMembers(team: Int, organization: String, success: (List<UserMD>) -> Unit, fail: (VolleyError) -> Unit) {
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
                success(members.map(mappers.userMapper::dbToMD))
            }
        }
    }

    fun getAvatarImageFromUrl(url: String, cb: (Bitmap) -> Unit ) {
        if(avatarCache.containsKey(url))
            return(cb(avatarCache[url]!!))

        val queue = app.queue
        val request = GetRequestImage(url,
                Response.Listener {
                    avatarCache[url] = it
                    cb(it)
                },
                Response.ErrorListener {
                    Toast.makeText(app, R.string.error_network, Toast.LENGTH_LONG).show()})
        queue.add(request)
    }


    fun getAvatarImageFromUrlSync(url: String):Bitmap? {
        val bitmap = avatarCache[url]
        if(bitmap==null)
            getAvatarImageFromUrl(url){} //TODO discutivel
        return bitmap
    }




    private fun getAvatarImageFromUser(user: UserMD, cb: () -> Unit ) {
        getAvatarImageFromUrl(user.avatar_url) {
            cb()
        }
    }

    fun getAvatarImageFromLogin(login : String, cb : () -> Unit) {
        fun success(user : UserMD) {
            getAvatarImageFromUser(user) {
                //avatarCache[currentUser.avatarUrl] = it
                cb()
            }
        }
        Log.d(app.TAG, "getAvatarImageFromLogin: Getting currentUser from DB")
        val user = localDb.userDAO().getUser(login)
        if (user == null) {
            Log.d(app.TAG, "getAvatarImageFromLogin: Getting currentUser from API")
            api.getUserDetailsForName(login, {
                Log.d(app.TAG, "getAvatarImageFromLogin: Got currentUser from API")
                userAvatarUrlCache[login] = it.avatar_url
                saveToDB(it).andThen { user ->
                    success(user)
                }
            }, {
                defaultErrorHandler(app)
            })
        } else {
            Log.d(app.TAG, "getAvatarImageFromLogin: Got currentUser from DB")
            userAvatarUrlCache[login] = user.avatarUrl
            success(mappers.userMapper.dbToMD(user))
        }
    }

    fun sendTeamMessage(team : TeamMD, message: MessageMD){
        firebase.sendMessage(mappers.messageMapper.mdToDto(message), team)
    }

    fun sendUserMessage(otherUserLogin: String, message: MessageMD) {
        firebase.sendUserMessage(mappers.messageMapper.mdToDto(message), otherUserLogin)
    }


}
