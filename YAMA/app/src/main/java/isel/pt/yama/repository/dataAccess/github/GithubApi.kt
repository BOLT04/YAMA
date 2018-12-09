package isel.pt.yama.repository.dataAccess.github

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.SP_NAME
import isel.pt.yama.repository.dataAccess.dto.OrganizationDto
import isel.pt.yama.repository.dataAccess.dto.TeamDto
import isel.pt.yama.repository.dataAccess.dto.UserDto
import isel.pt.yama.repository.dataAccess.network.*

const val GITHUB_API_HOST = "https://api.github.com"
const val GITHUB_API_USER = "$GITHUB_API_HOST/user"
const val GITHUB_API_USER_ORGS = "$GITHUB_API_HOST/user/orgs"
const val GITHUB_API_ORGS = "$GITHUB_API_HOST/orgs"
const val GITHUB_API_TEAMS = "$GITHUB_API_HOST/teams"
const val GITHUB_API_USER_NAME = "$GITHUB_API_HOST/users"


class GithubApi(private val app: YAMAApplication) : IGithubApi {
    // The responsibility to initiate the token is delegated to LoginActivity that
    // saves the token in shared preferences.
    var authHeaderMap: MutableMap<String, String>? = null
        get() {
            if (field == null) {
                // Then initialize the value with token in shared preference.
                val sharedPref = app.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

                val userTokenStr = app.getString(R.string.userToken)
                if (sharedPref.contains(userTokenStr)) {
                    val accessToken = sharedPref.getString(userTokenStr, null)
                    field = mutableMapOf(Pair("Authorization", "token $accessToken"))
                }
            }
            return field
        }


    override fun getUserDetails(accessToken : String, success: (UserDto) -> Unit, fail: (VolleyError) -> Unit) {
        getAndLog("getUserDetails: Fetching currentUser from Github API") {
            GetRequestUser(
                    GITHUB_API_USER,
                Response.Listener(success),
                Response.ErrorListener(fail),
                buildRequestHeaders(accessToken)
            )
        }
    }

    override fun getUserDetailsForName(name : String, success: (UserDto) -> Unit,
                                       fail: (VolleyError) -> Unit) {
        getAndLog("Fetching currentUser from Github API") {
            GetRequestUser(
                    "$GITHUB_API_USER_NAME/$name",
                Response.Listener(success),
                Response.ErrorListener(fail),
                authHeaderMap

            )
        }
    }

    override fun getUserOrganizations(accessToken: String,
                                      success: (List<OrganizationDto>) -> Unit,
                                      fail: (VolleyError) -> Unit) {
        getAndLog("getUserOrganizations: Fetching currentUser organizations from Github API") {
            GetRequestOrganizations(
                    GITHUB_API_USER_ORGS,
                Response.Listener(success),
                Response.ErrorListener(fail),
                buildRequestHeaders(accessToken)
            )
        }
    }

    override fun getTeams(orgId: String, success: (List<TeamDto>) -> Unit,
                          fail: (VolleyError) -> Unit) {
        getTeamsAux(orgId, authHeaderMap, success, fail)
    }

    override fun getTeamsWithToken(orgId: String, token: String,
                          success: Response.Listener<List<TeamDto>>, fail: Response.ErrorListener) {
        getTeamsAux(orgId, buildRequestHeaders(token), success::onResponse, fail::onErrorResponse)
    }

    private fun getTeamsAux(orgId: String, headers: MutableMap<String, String>?,
                            success: (List<TeamDto>) -> Unit, fail: (VolleyError) -> Unit) {
        getAndLog("getTeams: Fetching teams from Github API") {
            GetTeamsRequest(
                    "$GITHUB_API_ORGS/$orgId/teams",
                    Response.Listener(success),
                    Response.ErrorListener(fail),
                    headers
            )
        }
    }

    override fun getTeamMembersWithToken(teamId: Int, token: String,
                           success: Response.Listener<List<UserDto>>, fail: Response.ErrorListener) {
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

    override fun getTeamMembers(teamId: Int, success: (List<UserDto>) -> Unit,
                                fail: (VolleyError) -> Unit) {

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

    // Auxiliary functions
    private fun <T> getAndLog(msg: String, getReq: () -> Request<T>) {
        Log.v(app.TAG, msg)
        app.queue.add(getReq())
    }

    private fun buildRequestHeaders(accessToken: String) =
            mutableMapOf(Pair("Authorization", "token $accessToken"))
}