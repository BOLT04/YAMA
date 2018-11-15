package isel.pt.yama.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import isel.pt.yama.R
import isel.pt.yama.dto.Message
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

        val teamId=0 //TODO get team id selected, probably from intent?
        val user : UserDto? = null //TODO get user id selected, probably from intent?
        val viewModel = getViewModel("chat view model"){ //TODO extract to field
            ChatViewModel(app)
        }

        viewModel.init(user!!.id, teamId)


        sendBtn.setOnClickListener {
            val msg = userMessageTxt.text
            if (msg.isEmpty()) {
                Toast.makeText(this, "Please provide a message", Toast.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }

            viewModel.sendMessage(Message(user, msg.toString(), Date().time))
            userMessageTxt.text.clear()

        }
    }
}
