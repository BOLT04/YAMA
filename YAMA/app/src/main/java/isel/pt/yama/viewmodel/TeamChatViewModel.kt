package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.model.Message
import isel.pt.yama.repository.model.Team
import isel.pt.yama.repository.model.User


class TeamChatViewModel(val app : YAMAApplication,
                        private val repo : YAMARepository) : AndroidViewModel(app) {

    val user: User = app.repository.currentUser!!
    val team: Team = app.repository.team!!

    val chatLog: MutableLiveData<List<MutableLiveData<Message>>>
            = app.chatBoard.getTeamChat(team.id).liveData

    fun sendMessage(message: Message){
        repo.sendTeamMessage(team, message)
    }


}