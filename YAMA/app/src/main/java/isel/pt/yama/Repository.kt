package isel.pt.yama

import com.android.volley.RequestQueue
import com.android.volley.Response
import isel.pt.yama.dto.UserDto
import isel.pt.yama.network.GetRequest

// Holds all the data needed and interfaces with volley or any other data source.
object Repository {
    fun makeUserRequest(
            userToken: String,
            queue: RequestQueue,
            successListener: Response.Listener<UserDto>,
            errorListener: Response.ErrorListener) {

        val url = "https://api.github.com/user"
        val request = GetRequest(url, userToken, successListener, errorListener)

        queue.add(request)
    }
}