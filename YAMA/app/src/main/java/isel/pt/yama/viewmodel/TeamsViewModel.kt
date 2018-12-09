package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.model.Team

class TeamsViewModel(val app: YAMAApplication,
                     private val repo : YAMARepository) : AndroidViewModel(app) {

    val teams: MutableLiveData<List<Team>> = MutableLiveData()

    fun updateTeams() {
        repo.getTeams({
            teams.value = it
        }, {
            defaultErrorHandler(app)
        })
    }
}
