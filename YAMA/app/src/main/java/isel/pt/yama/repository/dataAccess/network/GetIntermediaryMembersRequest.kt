package isel.pt.yama.repository.dataAccess.network

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.pt.yama.repository.dataAccess.dto.IntermediaryUserDto

class GetIntermediaryMembersRequest(url: String, success: Response.Listener<List<IntermediaryUserDto>>, error: Response.ErrorListener,
                        private val headers: MutableMap<String, String>?)
    : JsonRequest<List<IntermediaryUserDto>>(Request.Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<List<IntermediaryUserDto>> {
        Log.v("YAMA DEBUG: rspnse.data",response.data.toString())
        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val usersDto = mapper.readValue<List<IntermediaryUserDto>>(String(response.data))
        Log.v("YAMA DEBUG", "usersDto.size: " + usersDto.size)
        return Response.success(usersDto, null)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }
}

/*
class GetMembersIntermediaryRequest(url: String,
                                    success: Response.Listener<List<IntermediaryUserDto>>,
                                    error: Response.ErrorListener,
                                    headers: MutableMap<String, String>?)

    : GetRequest<List<IntermediaryUserDto>>(List<IntermediaryUserDto>::class.java, url, success, error, headers)

*/