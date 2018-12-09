package isel.pt.yama.dataAccess.github

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.SP_NAME
import isel.pt.yama.dto.IntermediaryUserDto
import isel.pt.yama.dto.OrganizationDto
import isel.pt.yama.dto.TeamDto
import isel.pt.yama.dto.UserDto
import isel.pt.yama.network.*

const val GITHUB_API_HOST = "https://api.github.com"
const val GITHUB_API_USER = "$GITHUB_API_HOST/user"
const val GITHUB_API_USER_ORGS = "$GITHUB_API_HOST/user/orgs"
const val GITHUB_API_ORGS = "$GITHUB_API_HOST/orgs"
const val GITHUB_API_TEAMS = "$GITHUB_API_HOST/teams"
const val GITHUB_API_USER_NAME = "$GITHUB_API_HOST/users"

//TODO: move to package with better name -> dataAccess.cloud
class GithubApi(private val app: YAMAApplication) {
    // The responsibility to initiate the token is delegated to LoginActivity that
    // saves the token in shared preferences.
    var authHeaderMap: MutableMap<String, String>? = null
        get() {
            if (field == null) {
                // Then initialize the value with token in shared preference.
                val sharedPref = app.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                // TODO: can i get sharedPreferences like this...its a method that needs a context and it should be Activity or Aplication context?
                val userTokenStr = app.getString(R.string.userToken)
                if (sharedPref.contains(userTokenStr)) {
                    val accessToken = sharedPref.getString(userTokenStr, null)
                    field = mutableMapOf(Pair("Authorization", "token $accessToken"))// todo: Can this be initialized here?
                }
            }
            return field
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

    fun getUserDetails(accessToken : String, success: (UserDto) -> Unit, fail: (VolleyError) -> Unit) {//TODO: should we be coupled with VolleyError?
        getAndLog("getUserDetails: Fetching currentUser from Github API") {
            getRequestOf<UserDto>(
                    GITHUB_API_USER,
                Response.Listener(success),
                Response.ErrorListener(fail),
                buildRequestHeaders(accessToken)
            )
        }
    }
    fun getUserDetailsForName(name : String, success: (UserDto) -> Unit, fail: (VolleyError) -> Unit) {//TODO: should we be coupled with VolleyError?
        getAndLog("Fetching currentUser from Github API") {
            getRequestOf<UserDto>(
                    "$GITHUB_API_USER_NAME/$name",
                Response.Listener(success),
                Response.ErrorListener(fail),
                authHeaderMap

            )
        }
    }

    fun getUserOrganizations(accessToken: String, success: (List<OrganizationDto>) -> Unit, fail: (VolleyError) -> Unit) {
        getAndLog("getUserOrganizations: Fetching currentUser organizations from Github API") {
            getRequestOf<List<OrganizationDto>>(
                    GITHUB_API_USER_ORGS,
                Response.Listener(success),
                Response.ErrorListener(fail),
                buildRequestHeaders(accessToken)
            )
        }
    }

    fun getTeams(orgId: String, success: (List<TeamDto>) -> Unit, fail: (VolleyError) -> Unit) {
        getAndLog("getTeams: Fetching teams from Github API") {
            getRequestOf<List<TeamDto>>(
                "$GITHUB_API_ORGS/$orgId/teams",
                Response.Listener(success),
                Response.ErrorListener(fail),
                authHeaderMap
            )
        }
    }

    fun syncGetTeams(orgId: String, token: String, success: Response.Listener<List<TeamDto>>, fail: Response.ErrorListener) {
        getAndLog("syncGetTeams: Fetching teams from Github API") {
            GetTeamsRequest(
                    "$GITHUB_API_ORGS/$orgId/teams",
                    success,
                    fail,
                    buildRequestHeaders(token)
            )
        }
    }

    fun syncGetTeamMembers(teamId: Int, token: String, success: Response.Listener<List<UserDto>>, fail: Response.ErrorListener) {
        val userList = mutableListOf<UserDto>()
        getAndLog("syncGetTeamMembers: Fetching team members from Github API") {
            GetIntermediaryMembersRequest(

                    "$GITHUB_API_TEAMS/$teamId/members",
                    Response.Listener{
                        if(it.isEmpty())
                            success.onResponse(userList)

                        it.map { intermediaryUserDto ->
                            getUserDetailsForName(
                                    intermediaryUserDto.login,
                                    {userDto -> userList.add(userDto)
                                        if(userList.size==it.size)
                                            success.onResponse(userList)
                                    },
                                    fail::onErrorResponse)
                        }
                    },
                    fail,
                    buildRequestHeaders(token)
            )
        }
    }
    fun getTeamMembers(teamId: Int, success: (List<UserDto>) -> Unit, fail: (VolleyError) -> Unit) {

        val userList = mutableListOf<UserDto>()
        getAndLog("Fetching team members from Github API") {
            GetIntermediaryMembersRequest(

                "$GITHUB_API_TEAMS/$teamId/members",
                Response.Listener{
                    if(it.isEmpty())
                        success(userList)

                    it.map { intermediaryUserDto ->
                        getUserDetailsForName(
                                intermediaryUserDto.login,
                                {userDto -> userList.add(userDto)
                                    if(userList.size==it.size)
                                        success(userList)
                                },
                                fail)
                    }
                },
                Response.ErrorListener(fail),
                authHeaderMap
            )
        }
    }

    // Auxiliary funcions
    fun buildRequestHeaders(accessToken: String) =
            mutableMapOf(Pair("Authorization", "token $accessToken"))


}