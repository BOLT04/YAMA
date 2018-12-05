package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserMD


class ChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    val user: UserMD = app.repository.currentUser!!
    val team: TeamMD = app.repository.team!!

    val chatLog: MutableLiveData<List<MutableLiveData<MessageMD>>>
            = app.chatBoard.getTeamChat(team.id).liveData

    fun sendMessage(messageMD: MessageMD){
        app.repository.sendTeamMessage(team, messageMD)
    }


}