package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserMD

class MembersViewModel(val app: YAMAApplication) : AndroidViewModel(app) {

    val members: MutableLiveData<List<MutableLiveData<UserMD>>> = MutableLiveData()

    fun updateMembers(token: String, team: Int, organization: String) {
        app.repository.getTeamMembers(team, organization, {
            list ->
            members.value = list.map {
                    val ld = MutableLiveData<UserMD>()
                    ld.value = it
                    app.repository.getAvatarImageFromUrl(it.avatar_url){
                        b->ld.value=ld.value
                    }
                ld
            }
/*
    val team = app.repository.team!!
    val members: MutableLiveData<List<UserMD>> = MutableLiveData()

    fun updateMembers(token: String, organization: String) {
        app.repository.getTeamMembers(team.id, organization, {
            team.users = it
            members.value = it
*/
        }, {
            defaultErrorHandler(app)
        })
    }
}
