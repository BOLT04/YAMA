package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.UserMD

class UserChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    val user: UserMD = app.repository.currentUser!!
    private val otherUser: UserMD = app.repository.otherUser!!

    val chatLog: MutableLiveData<List<MutableLiveData<MessageMD>>>
            = app.chatBoard.getUserChat(otherUser.login).liveData

    fun sendMessage(messageMD: MessageMD){
        app.repository.sendUserMessage(otherUser.login, messageMD)
    }
}