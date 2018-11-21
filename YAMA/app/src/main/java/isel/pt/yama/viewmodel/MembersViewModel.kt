package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dto.UserDto

class MembersViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val members: MutableLiveData<List<UserDto>> = MutableLiveData()

    fun updateMembers(token: String, teamId: Int) {
        app.repository.getTeamMembers(teamId, {
            members.value = it
        }, {
            defaultErrorHandler(app)
        })
    }
}
