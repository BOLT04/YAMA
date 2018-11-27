package isel.pt.yama.model.dataAccess

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import isel.adeetc.pdm.currencywroom.kotlinx.AsyncWork
import isel.adeetc.pdm.currencywroom.kotlinx.runAsync
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.Organization
import isel.pt.yama.dto.Team
import isel.pt.yama.dto.UserDto
import isel.pt.yama.model.dataAccess.github.GithubApi
import isel.pt.yama.model.dataAccess.database.TeamDAO
import isel.pt.yama.network.*

 //TODO: THIS plus refactor name of method
fun syncSaveTeamsFromDTO(app: YAMAApplication, teamDao: TeamDAO, teams: List<Team>): List<Team> {
    Log.v(app.TAG, "Saving teams to DB")

    //val result = dto.quotes.map { Quote(it.currency, it.quote, date) }
    teamDao.insertAll(*teams.toTypedArray())//result.toTypedArray())
    return teams
}


// Holds all the data needed and interfaces with volley or any other data source.
class YAMARepository(private val app: YAMAApplication,
                     private val api: GithubApi,
                     private val teamDao: TeamDAO) {

    //TODO: implement this
    private fun saveToDB(teams: List<Team>): AsyncWork<List<Team>> {
        return runAsync {
            //TODO: what is success property???
            //if (dto.success) syncSaveTeamsFromDTO(app, teamDao, teams)
            //else listOf()
            syncSaveTeamsFromDTO(app, teamDao, teams)
        }
    }

    fun getUserDetails(accessToken : String, success: (UserDto) -> Unit, fail: (VolleyError) -> Unit) {
        // if (existsInCache()...) fetchFromCache(...)
        // else if (existsInDb...)fetchFromDb(...)
        api.getUserDetails(accessToken, {
            //saveToDb(it)
            success(it)
        }, fail)
    }

    fun getUserOrganizations(accessToken : String, success: (List<Organization>) -> Unit, fail: (VolleyError) -> Unit) {
        // if (existsInCache()...) fetchFromCache(...)
        // else if (existsInDb...)fetchFromDb(...)
        api.getUserOrganizations(accessToken, {
            //saveToDb(it)
            success(it)
        }, fail)
    }

    fun getTeams(orgId: String, success: (List<Team>) -> Unit, fail: (VolleyError) -> Unit) {
        runAsync {
            Log.v(app.TAG, "Getting teams from DB")
            teamDao.getOrganizationTeams(orgId)
        }.andThen { teams ->
            if (teams.isEmpty())// Then data isn't stored in db so fetch from API
                api.getTeams(orgId, {
                    //TODO: finish saving to db
                    saveToDB(it) andThen success
                    //success(it)
                }, fail)
            else {
                Log.v(app.TAG, "Got teams from DB")
                success(teams)
            }
        }
    }

    fun getTeamMembers(teamId: Int, success: (List<UserDto>) -> Unit, fail: (VolleyError) -> Unit) {
        // if (existsInCache()...) fetchFromCache(...)
        // else if (existsInDb...)fetchFromDb(...)
        api.getTeamMembers(teamId, {
            //saveToDb(it)
            success(it)
        }, fail)
    }

    var user: UserDto? = null

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
}