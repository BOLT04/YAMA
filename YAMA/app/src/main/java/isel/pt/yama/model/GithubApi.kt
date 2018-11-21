package isel.pt.yama.model

import android.util.JsonToken
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonRequest
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.Organization
import isel.pt.yama.dto.Team
import isel.pt.yama.dto.UserDto
import isel.pt.yama.network.GetMembersRequest
import isel.pt.yama.network.GetRequestOrganizations
import isel.pt.yama.network.GetRequestUser
import isel.pt.yama.network.GetTeamsRequest

const val GITHUB_API_HOST = "https://api.github.com"
const val GITHUB_API_USER = "$GITHUB_API_HOST/user"
const val GITHUB_API_ORGS = "$GITHUB_API_HOST/orgs"
const val GITHUB_API_TEAMS = "$GITHUB_API_HOST/teams"

class GithubApi(private val app: YAMAApplication) {
    lateinit var accessToken: String
    val authHeaderMap =
            mutableMapOf(Pair("Authorization", "token $accessToken"))// TODO: does this work

    // The responsibility to initiate the token is delegated to LoginViewModel
    //TODO: is this a good idea??? Delegate the initializing of access token, should we protect to multiple inits?

    fun init(token: String) {
        accessToken = token
    }

    fun <T> getAndLog(msg: String, getReq: () -> Request<T>) {
        Log.v(app.TAG, msg)
        app.queue.add(getReq())
    }

	//!!!!!!
	//TODO: Make a GitGetRequest class that has these authHeaderMap since any request for the github api needs this -> refactor code
	//!!!!!!
	//TODO: Perhaps also hide the Response.Listener and ErrorListener inside GetRequest -> the arguments in ctor are simplified to success, fail!!!!!!!!!!!!
	//!
	
    fun getUserDetails(success: (UserDto) -> Unit, fail: (VolleyError) -> Unit) {//TODO: should we be coupled with VolleyError?
        getAndLog("Fetching user from Github API") {
            GetRequestUser(
                GITHUB_API_USER,
                Response.Listener(success),
                Response.ErrorListener(fail),
                authHeaderMap
            )
        }
    }

    fun getUserOrganizations(success: (List<Organization>) -> Unit, fail: (VolleyError) -> Unit) {
        getAndLog("Fetching user organizations from Github API") {
            GetRequestOrganizations(
                GITHUB_API_ORGS,
                Response.Listener(success),
                Response.ErrorListener(fail),
                authHeaderMap
            )
        }
    }

    fun getTeams(orgId: String, success: (List<Team>) -> Unit, fail: (VolleyError) -> Unit) {
        getAndLog("Fetching teams  from Github API") {
            GetTeamsRequest(
                "$GITHUB_API_ORGS/$orgId/teams",
                Response.Listener(success),
                Response.ErrorListener(fail),
                authHeaderMap
            )
        }
    }

    fun getTeamMembers(teamId: Int, success: (List<UserDto>) -> Unit, fail: (VolleyError) -> Unit) {
        getAndLog("Fetching team members from Github API") {
            GetMembersRequest(
                "$GITHUB_API_TEAMS/$teamId/members",
                Response.Listener(success),
                Response.ErrorListener(fail),
                authHeaderMap
            )
        }
    }
}