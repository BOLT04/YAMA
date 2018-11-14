package isel.pt.yama.viewmodel

import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.network.GetTeamsRequest
import pt.isel.pdm.yama.model.Team

class TeamsViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val teams: MutableLiveData<List<Team>> = MutableLiveData()

    //TODO: Refactor code to use
    fun updateTeams(token: String, orgID: String) {
        //val queue = getApplication<YAMAApplication>().queue
        val request = GetTeamsRequest(
                "https://api.github.com/orgs/$orgID/teams",
                Response.Listener { teams.value = it },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                },
                mutableMapOf("Authorization" to "token $token"))

        app.queue.add(request)
    }
}
