package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.model.dataAccess.database.User

class MembersViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val members: MutableLiveData<List<User>> = MutableLiveData()

    fun updateMembers(token: String, team: Int, organization: String) {
        app.repository.getTeamMembers(team, organization, {
            members.value = it
        }, {
            defaultErrorHandler(app)
        })
    }
}
