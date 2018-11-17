package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.Repository
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.Message
import isel.pt.yama.dto.ReceivedMessage
import isel.pt.yama.dto.SentMessage
import isel.pt.yama.dto.UserDto


class ChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {
    val user : UserDto? = UserDto("Login", 1, "http://2.bp.blogspot.com/-CmBgofK7QzU/TVj3u3N1h2I/AAAAAAAADN8/OszBhGvvXRU/s640/tumblr_lg7h9gpbtP1qap9qio1_500.jpeg", "Name", null, null, 1, 1,"s") //TODO get user id selected, probably from intent?
    val anotheruser : UserDto? = UserDto("Login", 123, "http://2.bp.blogspot.com/-CmBgofK7QzU/TVj3u3N1h2I/AAAAAAAADN8/OszBhGvvXRU/s640/tumblr_lg7h9gpbtP1qap9qio1_500.jpeg", "Name", null, null, 1, 1,"s") //TODO get user id selected, probably from intent?

    val initchat = mutableListOf(
        ReceivedMessage(user!!, "some text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        SentMessage(user, "another text", 0),
        ReceivedMessage(anotheruser!!, "dj kalled text", 0)
    )




     private var  chatLogInternal: MutableLiveData<List<Message>>

    val chatLog: LiveData<List<Message>>
        get() = chatLogInternal





    init{

        var mutableLiveData = MutableLiveData<List<Message>>()

        chatLogInternal = mutableLiveData

        mutableLiveData.value= initchat
    }

    fun sendMessage(message: SentMessage){
        initchat.add(message)
        chatLogInternal.value=initchat

    }
   fun receiveChatMessage(message: ReceivedMessage){
       initchat.add(message)
       getApplication<YAMAApplication>().repository.getAvatarImage(message.user.avatar_url){message.userAvatar=it}
       chatLogInternal.value=initchat
   }




    fun getChatLog() = chatLogInternal.value

    fun getSpecificMessage(position: Int) = getChatLog()?.get(position)






/*


    var textUser: String? = null
    var textOrganization: String? = null
    var textToken: String? = null

    fun submitLogin() {
        getUserDetails()
        getUserOrganizations()
    }

    private fun tryLogin(): LiveData<Boolean> {

        return userInfo.combineAndCompute(userOrganizations) { a, b ->
            checkLoginInfo(a, b)
        }
    }

    private fun checkLoginInfo(usr : UserDto, orgs: List<Organization>) : Boolean {
        if (usr.login == textUser) {
            val organization = orgs.firstOrNull {
                it.login == textOrganization
            }
            if (organization != null)
                return true
        }
        return false
    }

    private fun getUserDetails() {
        val queue = getApplication<YAMAApplication>().queue
        val url = "https://api.github.com/user"

        val request = GetRequestUser(
                url,
                Response.Listener { userInfo.value = it },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                },
                mutableMapOf(Pair("Authorization", "token $textToken"))
        )

        queue.add(request)
    }

    private fun getUserOrganizations() {
        val queue = getApplication<YAMAApplication>().queue
        val url = "https://api.github.com/user/orgs"

        val request = GetRequestOrganizations(
                url,
                Response.Listener { userOrganizations.value = it },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                },
                mutableMapOf(Pair("Authorization", "token $textToken"))
        )
        queue.add(request)
    }*/
}