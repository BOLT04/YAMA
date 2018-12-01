package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.ReceivedMessage
import isel.pt.yama.dto.SentMessage



class ChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    val user: User? = app.repository.user
    val team: Team? = app.repository.team.value

    val chatLog: MutableLiveData<List<MessageDto>> = MutableLiveData()

    fun sendMessage(message: SentMessage){
        app.repository.sendMessage(message)
        app.repository.sendMessageToFirebase(message)
    }

/*    private fun receiveMessages(){
        app.repository.getMessagesFromFirebase()

    }

    fun receiveMessage(message: ReceivedMessage){
        initchat.add(message)
        getApplication<YAMAApplication>().repository.getAvatarImage(message.user.avatarUrl){message.userAvatar=it}
        chatLogInternal.value=initchat
    }*/

}