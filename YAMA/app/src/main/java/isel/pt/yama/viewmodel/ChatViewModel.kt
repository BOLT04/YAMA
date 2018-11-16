package isel.pt.yama.viewmodel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.activity.SP_NAME
import isel.pt.yama.dto.Message
import isel.pt.yama.dto.UserDto
import java.text.FieldPosition


class ChatViewModel(val app : YAMAApplication) : AndroidViewModel(app) {



    val chatLog: MutableLiveData<List<Message>> = MutableLiveData()

    var lastUserId = -1

    fun init(userId: Int){
        lastUserId=userId
    }

    fun sendMessage(message: Message){
        //getChatLog()?.add(message)
        chatLog.value= listOf(message)
    }
   // fun receiveChatMessage(teamId: Int, message: Message)= getChatLog()?.add(message)




    fun getChatLog() = chatLog.value

    fun getSpecificMessage(position: Int) = getChatLog()?.get(position)
    fun wasSent(position: Int)= getChatLog()?.get(position)?.user?.id?.equals(lastUserId)






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