package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dataAccess.database.Team

class TeamsViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val teams: MutableLiveData<List<Team>> = MutableLiveData()

    fun updateTeams() {
        app.repository.getTeams({
            teams.value = it
        }, {
            defaultErrorHandler(app)
        })
    }
}
