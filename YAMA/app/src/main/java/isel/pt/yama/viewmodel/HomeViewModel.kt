package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.model.Team
import isel.pt.yama.repository.model.UserAssociation

class HomeViewModel (val app: YAMAApplication,
                     private val repo : YAMARepository) : AndroidViewModel(app) {

    val teams: MutableLiveData<List<Team>> = MutableLiveData()
    val users: MutableLiveData<List<UserAssociation>> = MutableLiveData()

    fun updateChats() {
        updateTeams()
        updateUserChats()
    }

    fun updateTeams() {
        repo.getSubscribedTeams({
            teams.value = it
        }, {
            defaultErrorHandler(app)
        })
    }

    fun updateUserChats() {
        repo.getSubscribedUserChats({
            users.value = it
        }, {
            defaultErrorHandler(app)
        })
    }
}