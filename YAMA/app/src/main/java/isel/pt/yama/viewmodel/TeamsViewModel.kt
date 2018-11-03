package pt.isel.pdm.yama

import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.network.GetRequestTeams
import pt.isel.pdm.yama.model.Team

class TeamsViewModel (app: YAMAApplication) : AndroidViewModel(app) {

    val teams: MutableLiveData<List<Team>> = MutableLiveData()

    fun updateTeams(token : String, teamID : String) {
        val queue = getApplication<YAMAApplication>().queue
        val url = "https://api.github.com/orgs/$teamID/teams"

        val request = GetRequestTeams(url,
            Response.Listener { teams.value = it },
            Response.ErrorListener {
                Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
            },
            mutableMapOf(Pair("Authorization", "token $token")))

        queue.add(request)
    }
}
