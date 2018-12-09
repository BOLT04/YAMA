package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.repository.model.User

class MembersViewModel(val app: YAMAApplication) : AndroidViewModel(app) {


    val members: MutableLiveData<List<User>> = MutableLiveData()


    fun updateMembers(token: String, organization: String) {
        app.repository.getTeamMembers(
            app.repository.team!!.id
            , organization
            , {members.value = it}
            , {defaultErrorHandler(app)}
        )
    }
}
