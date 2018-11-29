package isel.pt.yama.network

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import isel.pt.yama.dto.OrganizationDto
import isel.pt.yama.dto.UserDto

class GetRequestUser(url: String, success: Response.Listener<UserDto>, error: Response.ErrorListener,
                     private val headers: MutableMap<String, String>?)
    : JsonRequest<UserDto>(Request.Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<UserDto> {
        Log.v("YAMA DEBUG: rspnse.data",response.data.toString())
        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val userDto = mapper
                .readValue<UserDto>(String(response.data))
        return Response.success(userDto, null)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }
}

//TESTTT
/*class GetRequestUser(url: String, success: Response.Listener<UserDto>, error: Response.ErrorListener,
                     private val headers: MutableMap<String, String>?)
    : GetRequest<UserDto>(url, success, error, headers)
*/

class GetRequestOrganizations(url: String, success: Response.Listener<List<OrganizationDto>>, error: Response.ErrorListener,
                              private val headers: MutableMap<String, String>?)
    : JsonRequest<List<OrganizationDto>>(Request.Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<List<OrganizationDto>> {
        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val organizationsDto = mapper
                .readValue<List<OrganizationDto>>(String(response.data))

        return Response.success(organizationsDto, null)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }
}

class GetRequestImage(url: String, success: Response.Listener<Bitmap>, error: Response.ErrorListener)
    : ImageRequest(url, success,0,0,ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error)


