package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserMD


class UserChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    val user: UserMD = app.repository.currentUser!!
    val otherUser: UserMD = app.repository.otherUser!!
    val team: TeamMD = app.repository.team!!

    val chatLog: MutableLiveData<List<MutableLiveData<MessageMD>>>
            = app.chatBoard.getUserChat(otherUser.login)?.liveData

    fun sendMessage(messageMD: MessageMD){
        app.repository.sendUserMessage(otherUser.login, messageMD)
    }


}