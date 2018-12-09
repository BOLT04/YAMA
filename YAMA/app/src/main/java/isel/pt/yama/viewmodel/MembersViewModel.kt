package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.model.User

class MembersViewModel(val app: YAMAApplication,
                       private val repo : YAMARepository) : AndroidViewModel(app) {


    val members: MutableLiveData<List<User>> = MutableLiveData()

    fun updateMembers(token: String, organization: String) {
        repo.getTeamMembers(
            app.repository.team!!.id
            , organization
            , {members.value = it}
            , {defaultErrorHandler(app)}
        )
    }
}
