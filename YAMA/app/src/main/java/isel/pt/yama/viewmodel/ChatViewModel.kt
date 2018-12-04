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
/*
class ChatViewModel(val app : YAMAApplication, val team: Team) : AndroidViewModel(app) {
    //TODO get user id selected, probably from intent?

    val user : User? = app.repository.user

    //here for proof of concept
    val anotheruser : User? = User("Login", 123, "Name", null, "http://2.bp.blogspot.com/-CmBgofK7QzU/TVj3u3N1h2I/AAAAAAAADN8/OszBhGvvXRU/s640/tumblr_lg7h9gpbtP1qap9qio1_500.jpeg", 1, 3) //TODO get user id selected, probably from intent?
    val initchat:MutableList<MutableLiveData<MessageDto>> = mutableListOf()



    private var  chatLogInternal: MutableLiveData<List<MutableLiveData<MessageDto>>>

    val chatLog: LiveData<List<MutableLiveData<MessageDto>>>
        get() = chatLogInternal


    init{

        var mutableLiveData = MutableLiveData<List<MutableLiveData<MessageDto>>>()

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
        receiveMessage(ReceivedMessage(anotheruser!!, "dj kalled text alksjdfhlaksdjhflkljg kljg lkljg lkjhg lkjg lkjg lkjg lkjg lkjglkjglkjg lkjg lkjgl kjglkjg lkjg lkjg lkjglkjglkjglkjg kasjdhflkjashdf laksjdh flkja lkjh lkjh lkjh lkjh lkjhlkjhlkjhlkjhlkj çoiuçoiyuçoiyupoiyupo poiypoiypo hsdlkfj halkfjh salkdfj hlkasjh dflkjhaslfdkjh a alksdhf lkashjd flk hasd", 0))
        receiveMessage(ReceivedMessage(user, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(user, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))
        receiveMessage(ReceivedMessage(anotheruser, "dj kalled text", 0))

*/

    val user: User? = app.repository.user
    val team: Team? = app.repository.team.value
    val chatLog: MutableLiveData<List<MessageDto>> = app.chatBoard.content[team?.name]!!

    fun sendMessage(message: MessageDto){
        app.repository.sendMessage(message)
    }



/*    fun sendMessage(message: SentMessage){
        app.repository.sendMessageToFirebase(message, team)
        val msg: MutableLiveData<MessageDto> = MutableLiveData()
        msg.value=message
        initchat.add(msg)
        chatLogInternal.value=initchat
    }

    fun receiveMessage(message: ReceivedMessage){
        val msg: MutableLiveData<MessageDto> = MutableLiveData()
        msg.value=message
        initchat.add(msg)
       getApplication<YAMAApplication>().repository.getAvatarImage(message.user.avatarUrl){message.userAvatar=it}
       chatLogInternal.value=initchat
    }
*/
}