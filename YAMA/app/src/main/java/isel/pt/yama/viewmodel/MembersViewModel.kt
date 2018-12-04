package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dataAccess.database.User

class MembersViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val members: MutableLiveData<List<MutableLiveData<User>>> = MutableLiveData()

    fun updateMembers(token: String, team: Int, organization: String) {
        app.repository.getTeamMembers(team, organization, {
            list ->
            members.value = list.map {
                    val ld = MutableLiveData<User>()
                    ld.value = it
                    app.repository.getAvatarImageFromUrl(it.avatarUrl){
                        b->ld.value=ld.value
                    }
                ld
            }
        }, {
            defaultErrorHandler(app)
        })
    }
}
