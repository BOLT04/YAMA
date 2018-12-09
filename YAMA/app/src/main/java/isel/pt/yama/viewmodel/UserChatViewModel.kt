package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.repository.model.Message
import isel.pt.yama.repository.model.User

class UserChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    val user: User = app.repository.currentUser!!
    private val otherUser: User = app.repository.otherUser!!

    val chatLog: MutableLiveData<List<MutableLiveData<Message>>>
            = app.chatBoard.getUserChat(otherUser.login).liveData

    fun sendMessage(message: Message){
        app.repository.sendUserMessage(otherUser.login, message)
    }
}