package isel.pt.yama.network

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.math.log
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



/**
 * logger parameter is called after the jackson mapper as finished reading the value.
 */
abstract class GetRequest<T>(url: String, success: Response.Listener<T>, error: Response.ErrorListener,
                             private val headers: MutableMap<String, String>?,
                             private val logger: (() -> Unit)? = null)
    : JsonRequest<T>(Request.Method.GET, url, "", success, error) {

    val TAG = "GetRequest"

    override fun parseNetworkResponse(response: NetworkResponse): Response< T> {
        Log.v(TAG, "parsing network response ${response.data}")

        //TODO: help with using generic type T here!
        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        //val type = mapper.typeFactory.constructParametricType(T::class.java)

        val resDto = mapper.readValue(String(response.data), object : TypeReference<T>(){} ) as T

        logger?.invoke()

        return Response.success(resDto, null)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }
}

/*
* abstract class GetRequest<T>(url: String, success: Response.Listener<T>, error: Response.ErrorListener,
                             private val headers: MutableMap<String, String>?,
                             private val logger: ((T) -> Unit)? = null)
    : JsonRequest<T>(Request.Method.GET, url, "", success, error) {

    val TAG = "GetRequest"

    override fun parseNetworkResponse(response: NetworkResponse): Response< T> {

        val dataStr = String(response.data)
        Log.v(TAG, "parsing network response $dataStr")

        val mapper = jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val resDto = mapper.readValue(dataStr, object : TypeReference<T>(){} ) as T

        logger?.invoke(resDto)

        return Response.success(resDto, null)
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers ?: super.getHeaders()
    }
}
*
*
* */