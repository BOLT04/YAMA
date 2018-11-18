package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.Message
import isel.pt.yama.dto.ReceivedMessage
import isel.pt.yama.dto.SentMessage
import isel.pt.yama.dto.UserDto


class ChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {
    //TODO get user id selected, probably from intent?

    val user : UserDto? = app.repository.user

    //here for proof of concept
    val anotheruser : UserDto? = UserDto("Login", 123, "http://2.bp.blogspot.com/-CmBgofK7QzU/TVj3u3N1h2I/AAAAAAAADN8/OszBhGvvXRU/s640/tumblr_lg7h9gpbtP1qap9qio1_500.jpeg", "Name", null, null, 1) //TODO get user id selected, probably from intent?
    val initchat:MutableList<Message> = mutableListOf()



    private var  chatLogInternal: MutableLiveData<List<Message>>

    val chatLog: LiveData<List<Message>>
        get() = chatLogInternal


    init{

        var mutableLiveData = MutableLiveData<List<Message>>()

        chatLogInternal = mutableLiveData

        mutableLiveData.value= initchat

        //here for proof of concept
        receiveMessage(ReceivedMessage(user!!, "some text", 0))
        sendMessage(SentMessage(user, "another text", 0))
        sendMessage(SentMessage(user, "another text", 0))
        sendMessage(SentMessage(user, "another text", 0))
        sendMessage(SentMessage(user, "another text", 0))
        sendMessage(SentMessage(user, "another text", 0))
        sendMessage(SentMessage(user, "another text", 0))
        sendMessage(SentMessage(user, "another text", 0))
        sendMessage(SentMessage(user, "another text", 0))
        receiveMessage(ReceivedMessage(anotheruser!!, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(user, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(user, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))


    }

    fun sendMessage(message: SentMessage){
        initchat.add(message)
        chatLogInternal.value=initchat

    }
   fun receiveMessage(message: ReceivedMessage){
       initchat.add(message)
       getApplication<YAMAApplication>().repository.getAvatarImage(message.user.avatar_url){message.userAvatar=it}
       chatLogInternal.value=initchat
   }

}