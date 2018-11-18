package isel.pt.yama.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.adapter.ChatAdapter
import isel.pt.yama.dto.ReceivedMessage
import isel.pt.yama.dto.UserDto
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    //TODO: make view model for this activity that holds a list of  an object containing sent message and avatar img of the user who sent it!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val app = getYAMAApplication()//TODO: is this a good solution? Should we override getApplication instead of making this extension?

        val user : UserDto? = UserDto("Login", 1,
                "http://2.bp.blogspot.com/-CmBgofK7QzU/TVj3u3N1h2I/AAAAAAAADN8/OszBhGvvXRU/s640/tumblr_lg7h9gpbtP1qap9qio1_500.jpeg",
                "Bear Boyo", null, null, 1) //TODO get user id selected, probably from intent?
        val viewModel = getViewModel("chat view model"){ //TODO extract to field
            ChatViewModel(app)
        }

        messagesList.layoutManager = LinearLayoutManager(this)
        messagesList.adapter = ChatAdapter(app, this, viewModel.chatLog)


        //messagesList.setHasFixedSize(true) // TODO: why do we use this and what does it do? Deeper understanding

        sendBtn.setOnClickListener {
            val msg = userMessageTxt.text
            if (msg.isEmpty()) {
                Toast.makeText(this, "Please provide a message", Toast.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }

            viewModel.receiveChatMessage(ReceivedMessage(user!!, msg.toString(), Date().time))
            userMessageTxt.text.clear()

        }
    }
}
