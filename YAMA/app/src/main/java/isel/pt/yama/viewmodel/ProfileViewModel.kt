package isel.pt.yama.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.UserDto
import isel.pt.yama.network.GetRequest

class ProfileViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    var userLiveData: MutableLiveData<UserDto> = MutableLiveData()// TODO: what gets stored here

    fun makeRequest(userID: String, orgID: String, userToken: String) {
        val queue = getApplication<YAMAApplication>().queue
        val url = "https://api.github.com/user"
/*
        val request = GetRequest(url, userToken,
                Response.Listener {
                    val sharedPref = getApplication<>()?.getPreferences(Context.MODE_PRIVATE) ?: return
                    with (sharedPref.edit()) {
                        putInt(getString(R.string.saved_high_score_key), it.id)
                        putInt(getString(R.string.saved_high_score_key), it.login)
                        putString(getString(R.string.saved_high_score_key), orgId)
                        apply()
                    }
                    userLiveData.value = it
                },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                })

        queue.add(request)
        */
    }
}