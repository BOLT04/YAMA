package isel.pt.yama.repository.dataAccess.github

import com.android.volley.Response
import com.android.volley.VolleyError
import isel.pt.yama.repository.dataAccess.dto.OrganizationDto
import isel.pt.yama.repository.dataAccess.dto.TeamDto
import isel.pt.yama.repository.dataAccess.dto.UserDto

interface IGithubApi {
    fun getUserDetails(accessToken : String, success: (UserDto) -> Unit, fail: (VolleyError) -> Unit)
    fun getUserDetailsForName(name : String, success: (UserDto) -> Unit, fail: (VolleyError) -> Unit)
    fun getUserOrganizations(accessToken: String, success: (List<OrganizationDto>) -> Unit,
                             fail: (VolleyError) -> Unit)
    fun getTeams(orgId: String, success: (List<TeamDto>) -> Unit, fail: (VolleyError) -> Unit)
    fun getTeamsWithToken(orgId: String, token: String,
                          success: Response.Listener<List<TeamDto>>, fail: Response.ErrorListener)
    fun getTeamMembers(teamId: Int, success: (List<UserDto>) -> Unit, fail: (VolleyError) -> Unit)

    fun getTeamMembersWithToken(teamId: Int, token: String,
                                success: Response.Listener<List<UserDto>>, fail: Response.ErrorListener)
}