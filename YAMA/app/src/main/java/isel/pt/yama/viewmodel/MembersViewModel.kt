package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserMD

class MembersViewModel(val app: YAMAApplication) : AndroidViewModel(app) {


    val members: MutableLiveData<List<UserMD>> = MutableLiveData()


    fun updateMembers(token: String, organization: String) {
        app.repository.getTeamMembers(
            app.repository.team!!.id
            , organization
            , {members.value = it}
            , {defaultErrorHandler(app)}
        )
    }
}
