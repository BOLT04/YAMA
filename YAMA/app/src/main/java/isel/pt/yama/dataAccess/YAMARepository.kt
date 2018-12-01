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
import isel.pt.yama.dataAccess.database.*
import isel.pt.yama.dataAccess.github.GithubApi
import isel.pt.yama.dto.OrganizationDto
import isel.pt.yama.dto.SentMessage
import isel.pt.yama.dto.TeamDto
import isel.pt.yama.dto.UserDto
import isel.pt.yama.dataAccess.firebase.FirebaseDatabase
import isel.pt.yama.network.GetRequestImage


// Holds all the data needed and interfaces with volley or any other data source.
class YAMARepository(private val app: YAMAApplication,
                     private val api: GithubApi,
                     private val localDb: YAMADatabase,
                     private val firebase: FirebaseDatabase) {




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
        Log.v(app.TAG, "Saving teams to DB")

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
        Log.v(app.TAG, "Saving organizations to DB")
        val result = organizations.map { dto ->
            Organization(dto.login, dto.id)
        }
        db.organizationDAO().insertAll(*result.toTypedArray())
        return result
    }
    private fun syncSaveUserFromDTO(app: YAMAApplication, db: YAMADatabase, user: UserDto): User {
        Log.v(app.TAG, "Saving user to DB")
        val result = User(user.login,user.id, user.name, user.email, user.avatar_url, user.followers, user.following)
        db.userDAO().insertUsers(result)
        return result
    }

    private fun syncSaveUserOrganizationsFromDTO(app: YAMAApplication, db: YAMADatabase, user: String, organizations: List<OrganizationDto>): List<Organization> {
        Log.v(app.TAG, "Saving user organizations to DB")

        val data = organizations.map { dto ->
            OrganizationMember(dto.login, user)
        }

        db.organizationMembersDAO().insertAll(*data.toTypedArray())

        return organizations.map { Organization(it.login, it.id) }
    }

    fun getUserDetails(userLogin: String, accessToken : String, success: (User) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(app.TAG, "Getting user from DB")
            localDb.userDAO().getUser(userLogin)
        }.andThen { user ->
            if (user == null)
                api.getUserDetails(accessToken, {
                    saveToDB(it).andThen(success)
                }, fail)
            else {
                Log.v(app.TAG, "Got user from DB")
                success(user)
            }
        }
    }

    fun getUserOrganizations(user: String, accessToken : String, success: (List<Organization>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(app.TAG, "Getting organizations from DB")
            localDb.organizationMembersDAO().getUserOrganizations(user)
        }.andThen { organizations ->
            if (organizations.isEmpty())
                api.getUserOrganizations(accessToken, {
                     saveToDBUSer(user, it).andThen(success)
            }, fail)
            else {
                Log.v(app.TAG, "Got organizations from DB")
                success(organizations)
            }
        }
    }

    fun getTeams(organization: String, success: (List<Team>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(app.TAG, "Getting teams from DB")
            localDb.teamDAO().getOrganizationTeams(organization)
        }.andThen { teams ->
            if (teams.isEmpty())// Then data isn't stored in localDb so fetch from API
                api.getTeams(organization, {
                    //TODO: finish saving to localDb
                    saveToDB(organization, it).andThen(success)
                }, fail)
            else {
                Log.v(app.TAG, "Got teams from DB")
                success(teams)
            }
        }
    }

    fun getTeamMembers(team: Int, organization: String, success: (List<User>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(app.TAG, "Getting team members from DB")
            localDb.teamMembersDAO().getTeamMembers(team, organization)
        }.andThen { members ->
            if (members.isEmpty())
                api.getTeamMembers(team, {
                    saveToDB(team, organization, it).andThen(success)
                }, fail)
            else {
                Log.v(app.TAG, "Got organizations from DB")
                success(members)
            }
        }
    }

    var user: User? = null

    private val avatarCache : HashMap<String, Bitmap> = HashMap()

    fun getAvatarImage(url: String, cb: (Bitmap) -> Unit ) {

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


    fun sendMessageToFirebase(message: SentMessage, team: Team){

        firebase.sendMessage(message, team)
    }

}