package isel.pt.yama.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.adapter.ChatAdapter
import isel.pt.yama.dto.SentMessage
import isel.pt.yama.dto.TeamDto
import isel.pt.yama.dto.UserDto
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.model.dataAccess.database.Team
import isel.pt.yama.model.dataAccess.database.User
import isel.pt.yama.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    //TODO: make view model for this activity that holds a list of  an object containing sent message and avatar img of the user who sent it!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val app = getYAMAApplication()//TODO: is this a good solution? Should we override getApplication instead of making this extension?

        val user : User = app.repository.user!!

        val team: Team = intent.getParcelableExtra("team")//TODO: what to put on default value

        val viewModel = getViewModel("chat view model"){ //TODO extract to field
            ChatViewModel(app, team)
        }


        teamName.text=team.name

        messagesList.layoutManager = LinearLayoutManager(this)
        messagesList.adapter = ChatAdapter(app, this, viewModel.chatLog)



        sendBtn.setOnClickListener {
            val msg = userMessageTxt.text
            if (msg.isEmpty()) {
                return@setOnClickListener
            }

            viewModel.sendMessage(SentMessage(user, msg.toString(), Date().time))
            userMessageTxt.text.clear()
        }

        teamName.setOnClickListener{
            val intent = Intent(this, MembersActivity::class.java)
            intent.putExtra("team", team)
            startActivity(intent)
        }

    }
}
