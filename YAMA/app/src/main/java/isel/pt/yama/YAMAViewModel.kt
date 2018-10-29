package isel.pt.yama

import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.android.volley.Response
import isel.pt.yama.network.GetRequest

class YAMAViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    fun makeRequest(userID: String, orgID: String, userToken: String) {
        val queue = getApplication<YAMAApplication>().queue
        val url = "https://api.github.com/user"

        val request = GetRequest(url, userToken,
                Response.Listener {
                    app.userLiveData.value = it
                },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                })

        queue.add(request)
    }
}