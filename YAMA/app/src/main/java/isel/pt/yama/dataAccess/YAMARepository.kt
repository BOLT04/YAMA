package isel.pt.yama.dataAccess

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
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
import isel.pt.yama.dto.*
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.ReceivedMessageMD
import isel.pt.yama.model.SentMessageMD
import isel.pt.yama.network.GetRequestImage


// Holds all the data needed and interfaces with volley or any other data source.
class YAMARepository(private val app: YAMAApplication,
                     private val api: GithubApi,
                     private val localDb: YAMADatabase,
                     private val firebase: FirebaseDatabase) {

    var token: String = ""
    var organization: String = ""
    var user: User? = null
    var team: MutableLiveData<Team> = MutableLiveData()
    val avatarCache : HashMap<String, Bitmap> = HashMap()
    val userAvatarUrlCache : HashMap<String, String> = HashMap()
    val TAG = YAMARepository::class.java.simpleName


    //TODO: implement this
    private fun saveToDB(orgId: String, teams: List<TeamDto>): AsyncWork<List<Team>> {
        return runAsync {
            //TODO: what is success property???
            //if (dto.success) syncSaveTeamsFromDTO(app, teamDao, teams)
            //else listOf()
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

    private fun saveToDBUSer(user : String, organizations: List<OrganizationDto>): AsyncWork<List<Organization>> {
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
            Team(dto.name, dto.id, organization, dto.description)
        }
        db.teamDAO().insertAll(*result.toTypedArray())//result.toTypedArray())
        return result
    }
  

    private fun syncSaveTeamMemberFromDTO(app: YAMAApplication, db: YAMADatabase, team: Int, organization: String, members: List<UserDto>): List<User> {
        Log.v(app.TAG, "Saving team members to DB")

        members.forEach {
            if (db.userDAO().getUser(it.login) == null)
                db.userDAO().insertUser(
                        User(it.login, it.id, it.name, it.email, it.avatar_url, it.followers, it.following)
                )
            db.teamMembersDAO().insert(TeamMember(organization, team, it.login))
        }

        return db.teamMembersDAO().getTeamMembers(team, organization)

    }
    private fun syncSaveOrganizationsFromDTO(app: YAMAApplication, db: YAMADatabase, organizations: List<OrganizationDto>): List<Organization> {
        Log.v(app.TAG, "syncSaveOrganizationsFromDTO: Saving organizations to DB")
        val result = organizations.map { dto ->
            Organization(dto.login, dto.id)
        }
        db.organizationDAO().insertAll(*result.toTypedArray())
        return result
    }
    private fun syncSaveUserFromDTO(app: YAMAApplication, db: YAMADatabase, user: UserDto): User {
        Log.v(app.TAG, "syncSaveUserFromDTO: Saving user to DB")
        val result = User(user.login,user.id, user.name, user.email, user.avatar_url, user.followers, user.following)
        db.userDAO().insertUsers(result)
        return result
    }

    private fun syncSaveUserOrganizationsFromDTO(app: YAMAApplication, db: YAMADatabase, user: String, organizations: List<OrganizationDto>): List<Organization> {
        Log.v(TAG, "Saving user organizations to DB")

        val data = organizations.map { dto ->
            OrganizationMember(dto.login, user)
        }

        db.organizationMembersDAO().insertAll(*data.toTypedArray())

        return organizations.map { Organization(it.login, it.id) }
    }

    fun getUserDetails(userLogin: String, accessToken : String, success: (User) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(TAG, "Getting user from DB")

            localDb.userDAO().getUser(userLogin)
        }.andThen { user ->
            if (user == null)
                api.getUserDetails(accessToken, {
                    userAvatarUrlCache[userLogin] = it.avatar_url
                    saveToDB(it).andThen(success)
                }, fail)
            else {
                Log.v(app.TAG, "getUserDetails: Got user from DB")
                userAvatarUrlCache[userLogin] = user.avatarUrl

                success(user)
            }
        }
    }

    fun getUser(userLogin: String, success: (User) -> Unit, fail: (VolleyError) -> Unit) {

        runAsync {
            Log.v(TAG, "Getting user from DB")

            localDb.userDAO().getUser(userLogin)
        }.andThen { user ->
            if (user == null)
                api.getUserDetailsForName(userLogin, {
                    userAvatarUrlCache[userLogin] = it.avatar_url
                    saveToDB(it).andThen(success)
                }, fail)
            else {
                Log.v(app.TAG, "getUserDetails: Got user from DB")
                userAvatarUrlCache[userLogin] = user.avatarUrl

                success(user)
            }
        }
    }






    fun getUserOrganizations(user: String, accessToken : String, success: (List<Organization>) -> Unit, fail: (VolleyError) -> Unit) {
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
                success(organizations)
            }
        }
    }

    fun getTeams(success: (List<Team>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(TAG, "Getting teams from DB")
            localDb.teamDAO().getOrganizationTeams(organization)
        }.andThen { teams ->
            if (teams.isEmpty())// Then data isn't stored in localDb so fetch from API
                api.getTeams(organization, {
                    //TODO: finish saving to localDb
                    saveToDB(organization, it).andThen(success)
                }, fail)
            else {
                Log.v(TAG, "Got teams from DB")
                success(teams)
            }
        }
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
                success(members)
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


    fun getAvatarImageFromUrlSync(url: String):Bitmap? =
            avatarCache[url]




    private fun getAvatarImageFromUser(user: User, cb: () -> Unit ) {
        getAvatarImageFromUrl(user.avatarUrl) {
            cb()
        }
    }

    fun getAvatarImageFromLogin(login : String, cb : () -> Unit) {
        fun success(user : User) {
            getAvatarImageFromUser(user) {
                //avatarCache[user.avatarUrl] = it
                cb()
            }
        }
        Log.d(app.TAG, "getAvatarImageFromLogin: Getting user from DB")
        val user = localDb.userDAO().getUser(login)
        if (user == null) {
            Log.d(app.TAG, "getAvatarImageFromLogin: Getting user from API")
            api.getUserDetailsForName(login, {
                Log.d(app.TAG, "getAvatarImageFromLogin: Got user from API")
                userAvatarUrlCache[login] = it.avatar_url
                saveToDB(it).andThen { user ->
                    success(user)
                }
            }, {
                defaultErrorHandler(app)
            })
        } else {
            Log.d(app.TAG, "getAvatarImageFromLogin: Got user from DB")
            userAvatarUrlCache[login] = user.avatarUrl
            success(user)
        }
    }

    private fun sendMessageToFirebase(message: MessageDto){
        firebase.sendMessage(message, team.value!!)
    }

    fun sendMessage(messageMD: MessageMD) {
        sendMessageToFirebase(messsageToDto(messageMD))
    }

    /*
    fun sendMessageToFirebase(messageMD: SentMessage, team: Team){
        firebase.sendMessage(messageMD, team)
    }

    */



    ////////////////////////////////////

    public fun messsageToDto(messageMD: MessageMD):MessageDto =
            MessageDto(messageMD.user.login,
                    messageMD.content,
                    messageMD.createdAt)

     public fun dtoToMessage(dto: MessageDto, cb: (MessageMD) -> Unit) =
             getUser(dto.user,
                     {
                         if( it == user)
                            cb(SentMessageMD(it, dto.content, dto.createdAt))
                         else
                             cb(ReceivedMessageMD(it, dto.content, dto.createdAt))

                     },
                     { error -> throw Error(error)}//TODO shady biz
    )












}
