package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.model.Message
import isel.pt.yama.repository.model.User

class UserChatViewModel(app : YAMAApplication,
                        private val repo : YAMARepository) : AndroidViewModel(app) {

    val user: User = app.repository.currentUser!!
    private val otherUser: User = app.repository.otherUser!!

    val chatLog: MutableLiveData<List<MutableLiveData<Message>>>
            = app.chatBoard.getUserChat(otherUser.login).liveData

    fun sendMessage(message: Message){
        repo.sendUserMessage(otherUser.login, message)
    }
}