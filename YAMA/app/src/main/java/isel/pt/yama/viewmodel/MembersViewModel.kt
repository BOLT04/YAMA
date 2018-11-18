package isel.pt.yama.viewmodel

import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.UserDto
import isel.pt.yama.network.GetMembersRequest

class MembersViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val members: MutableLiveData<List<UserDto>> = MutableLiveData()

    //TODO: Refactor code to use
    fun updateMembers(token: String, teamID: Int) {
        //val queue = getApplication<YAMAApplication>().queue
        val request = GetMembersRequest(
                "https://api.github.com/teams/$teamID/members",
                Response.Listener { members.value = it },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                },
                mutableMapOf("Authorization" to "token $token"))

        app.queue.add(request)
    }

}
