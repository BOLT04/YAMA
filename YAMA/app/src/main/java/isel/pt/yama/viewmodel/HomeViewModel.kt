package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dataAccess.firebase.ChatBoard
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserAssociation
import isel.pt.yama.model.UserMD

class HomeViewModel (val app: YAMAApplication) : AndroidViewModel(app) {

    val teams: MutableLiveData<List<TeamMD>> = MutableLiveData()
    val users: MutableLiveData<List<UserAssociation>> = MutableLiveData()

    fun updateChats() {
        updateTeams()
        updateUserChats()
    }

    fun updateTeams() {
        app.repository.getSubscribedTeams({
            teams.value = it
        }, {
            defaultErrorHandler(app)
        })
    }

    fun updateUserChats() {
        app.repository.getSubscribedUserChats({
            users.value = it
        }, {
            defaultErrorHandler(app)
        })
    }
}