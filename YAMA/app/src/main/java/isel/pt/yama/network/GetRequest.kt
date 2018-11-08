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
import isel.pt.yama.dto.UserDto
import pt.isel.pdm.yama.model.Organization

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

class GetRequestOrganizations(url: String, success: Response.Listener<List<Organization>>, error: Response.ErrorListener,
                              private val headers: MutableMap<String, String>?)
    : JsonRequest<List<Organization>>(Request.Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<List<Organization>> {
        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val organizationsDto = mapper
                .readValue<List<Organization>>(String(response.data))

        return Response.success(organizationsDto, null)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }
}

class GetRequestImage(url: String, success: Response.Listener<Bitmap>, error: Response.ErrorListener)
    : ImageRequest(url, success,0,0,ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, error)

/*
class GetRequestTeams(url: String, success: Response.Listener<List<Team>>, error: Response.ErrorListener,
                      private val headers: MutableMap<String, String>?)
    : JsonRequest<List<Team>>(Request.Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<List<Team>> {
        Log.v("YAMA DEBUG: rspnse.data",response.data.toString())
        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val teamsDto = mapper
                .readValue<List<Team>>(String(response.data))
        return Response.success(teamsDto, null)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }
}*/
