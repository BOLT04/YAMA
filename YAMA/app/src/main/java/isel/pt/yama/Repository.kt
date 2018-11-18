package isel.pt.yama

import android.graphics.Bitmap
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import isel.pt.yama.dto.UserDto
import isel.pt.yama.network.GetRequestImage

// Holds all the data needed and interfaces with volley or any other data source.
class Repository(val app: YAMAApplication) {
    fun makeUserRequest(
            userToken: String,
            queue: RequestQueue,
            successListener: Response.Listener<UserDto>,
            errorListener: Response.ErrorListener) {

        val url = "https://api.github.com/user"
       // val request = GetRequest(url, userToken, successListener, errorListener)

       // queue.add(request)
    }
    val avatarCache : HashMap<String, Bitmap> = HashMap()

    fun getAvatarImage(url: String, cb: (Bitmap) -> Unit ) {

        if(avatarCache.containsKey(url))
            return(cb(avatarCache[url]!!))

        val queue = app.queue
        val request = GetRequestImage(url,
                Response.Listener {
                    avatarCache[url] = it
                    cb(it)
                },
                Response.ErrorListener {
                    Toast.makeText(app, R.string.error_network, Toast.LENGTH_LONG).show()})
        queue.add(request)
    }
}