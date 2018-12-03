package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.ReceivedMessage



class ChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    val user: User? = app.repository.user
    val team: Team? = app.repository.team.value
    val chatLog: MutableLiveData<List<MessageDto>> = app.chatBoard.content

    fun sendMessage(message: MessageDto){
        app.repository.sendMessage(message)
    }


}