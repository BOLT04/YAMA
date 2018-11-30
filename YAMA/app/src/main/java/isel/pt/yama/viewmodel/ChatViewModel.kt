package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.ReceivedMessage
import isel.pt.yama.dto.SentMessage
import isel.pt.yama.model.dataAccess.database.Team
import isel.pt.yama.model.dataAccess.database.User


class ChatViewModel(val app : YAMAApplication, val team: Team) : AndroidViewModel(app) {
    //TODO get user id selected, probably from intent?

    val user : User? = app.repository.user

    //here for proof of concept
    val anotheruser : User? = User("Login", 123, "Name", null, "http://2.bp.blogspot.com/-CmBgofK7QzU/TVj3u3N1h2I/AAAAAAAADN8/OszBhGvvXRU/s640/tumblr_lg7h9gpbtP1qap9qio1_500.jpeg", 1, 3) //TODO get user id selected, probably from intent?
    val initchat:MutableList<MessageDto> = mutableListOf()



    private var  chatLogInternal: MutableLiveData<List<MessageDto>>

    val chatLog: LiveData<List<MessageDto>>
        get() = chatLogInternal


    init{

        var mutableLiveData = MutableLiveData<List<MessageDto>>()

        chatLogInternal = mutableLiveData

        mutableLiveData.value= initchat

        //here for proof of concept
        receiveMessage(ReceivedMessage(user!!, "some content", 0))
        sendMessage(SentMessage(user, "another one", 0))
        sendMessage(SentMessage(user, "another one", 0))
        sendMessage(SentMessage(user, "another one", 0))
        sendMessage(SentMessage(user, "another one", 0))
        sendMessage(SentMessage(user, "another one", 0))
        sendMessage(SentMessage(user, "another one", 0))
        sendMessage(SentMessage(user, "another one", 0))
        sendMessage(SentMessage(user, "another one", 0))
        receiveMessage(ReceivedMessage(anotheruser!!, "dj kalled", 0))
        receiveMessage(ReceivedMessage(user, "dj kalled", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled", 0))
        receiveMessage(ReceivedMessage(user, "dj kalled content", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled ", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled ", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled ", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled ", 0))


    }

    fun sendMessage(message: SentMessage){
        app.repository.sendMessageToFirebase(message, team)

        initchat.add(message)
        chatLogInternal.value=initchat

    }
   fun receiveMessage(message: ReceivedMessage){
       initchat.add(message)
       getApplication<YAMAApplication>().repository.getAvatarImage(message.user.avatarUrl){message.userAvatar=it}
       chatLogInternal.value=initchat
   }

}