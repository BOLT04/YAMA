package isel.pt.yama.repository.dataAccess.network

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.pt.yama.repository.dataAccess.dto.TeamDto

class GetTeamsRequest(url: String, success: Response.Listener<List<TeamDto>>, error: Response.ErrorListener,
                      private val headers: MutableMap<String, String>?)
    : JsonRequest<List<TeamDto>>(Request.Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<List<TeamDto>> {
        Log.v("YAMA DEBUG: rspnse.data",response.data.toString())
        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val teamsDto = mapper.readValue<List<TeamDto>>(String(response.data))
        return Response.success(teamsDto, null)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }
}