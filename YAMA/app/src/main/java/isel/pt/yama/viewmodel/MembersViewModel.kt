package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserMD

class MembersViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val team = app.repository.team!!
    val members: MutableLiveData<List<UserMD>> = MutableLiveData()

    fun updateMembers(token: String, organization: String) {
        app.repository.getTeamMembers(team.id, organization, {
            team.users = it
            members.value = it
        }, {
            defaultErrorHandler(app)
        })
    }
}
