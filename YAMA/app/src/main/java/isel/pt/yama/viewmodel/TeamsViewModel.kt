package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.model.dataAccess.database.Team

class TeamsViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val teams: MutableLiveData<List<Team>> = MutableLiveData()

    fun updateTeams(token: String, organization: String) {
        app.repository.getTeams(organization, {
            teams.value = it
        }, {
            defaultErrorHandler(app)
        })
    }
}
