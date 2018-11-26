package isel.pt.yama.model

import android.graphics.Bitmap
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.Organization
import isel.pt.yama.dto.Team
import isel.pt.yama.dto.UserDto
import isel.pt.yama.model.dataAccess.database.TeamDAO
import isel.pt.yama.network.*

// Holds all the data needed and interfaces with volley or any other data source.
class YAMARepository(private val app: YAMAApplication,
                     private val api: GithubApi,
                     private val teamDao: TeamDAO) {
    /*
    private val db = Room
            .databaseBuilder(app, CurrenciesDatabase::class.java, "quotes-db")
            .build()
    */
//TODO: implement DB with room
    fun getUserDetails(success: (UserDto) -> Unit, fail: (VolleyError) -> Unit) {
        // if (existsInCache()...) fetchFromCache(...)
        // else if (existsInDb...)fetchFromDb(...)
        api.getUserDetails( {
            //saveToDb(it)
            success(it)
        }, fail)
    }

    fun getUserOrganizations(success: (List<Organization>) -> Unit, fail: (VolleyError) -> Unit) {
        // if (existsInCache()...) fetchFromCache(...)
        // else if (existsInDb...)fetchFromDb(...)
        api.getUserOrganizations( {
            //saveToDb(it)
            success(it)
        }, fail)
    }

    fun getTeams(orgId: String, success: (List<Team>) -> Unit, fail: (VolleyError) -> Unit) {
        // if (existsInCache()...) fetchFromCache(...)
        // else if (existsInDb...)fetchFromDb(...)
        api.getTeams(orgId, {
            //saveToDb(it)
            success(it)
        }, fail)
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