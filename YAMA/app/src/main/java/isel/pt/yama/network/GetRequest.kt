package isel.pt.yama.network

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import isel.pt.yama.dto.UserDto

class GetRequest(
        url: String,
        private val userToken: String,
        success: Response.Listener<UserDto>,
        error: Response.ErrorListener)
    : JsonRequest<UserDto>(Request.Method.GET, url, "", success, error) {

    companion object {
        fun mapStringToDto(jsonString: String): UserDto? {
            val mapper = jacksonObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            return mapper.readValue(jsonString, UserDto::class.java)
        }
    }

    // Uses Jackson to parse the response from the Github API into an UserDto object.
    override fun parseNetworkResponse(response: NetworkResponse): Response<UserDto> {
        val userDto = mapResponseToJson(response)
        return Response.success(userDto, null)
    }

    private fun mapResponseToJson(response: NetworkResponse): UserDto? =
            mapStringToDto(String(response.data))

    override fun getHeaders(): MutableMap<String, String> =
            mutableMapOf("Authorization" to "token $userToken")
}