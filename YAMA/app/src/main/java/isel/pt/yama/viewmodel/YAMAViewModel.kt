package isel.pt.yama.viewmodel

import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.UserDto
import isel.pt.yama.network.GetRequest

class YAMAViewModel(val app : YAMAApplication) : AndroidViewModel(app) {
    // TODO: if this live data doesnt exist then we cant observe changes in activity and the startActivity with intent goes to makeRequest()
    val userLiveData: MutableLiveData<UserDto> = MutableLiveData()// TODO: what gets stored here

    fun makeRequest(sharedPref: SharedPreferences, userID: String, orgID: String, userToken: String) {
        val queue = getApplication<YAMAApplication>().queue
        val url = "https://api.github.com/user"

        //TODO: this goes into Repository object and then we dont need to pass the user through intetns
        val request = GetRequest(url, userToken,
                Response.Listener {
                    with (sharedPref.edit()) {
                        //TODO: do we need to save more stuff in shared prefs?
                        putString(app.getString(R.string.userId), userID) //TODO: do we save the parameter or it.login
                        putString(app.getString(R.string.organizationId), orgID)
                        putString(app.getString(R.string.userToken), userToken)
                        apply()
                    }
                    userLiveData.value = it
                },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                })

        queue.add(request)
    }
}