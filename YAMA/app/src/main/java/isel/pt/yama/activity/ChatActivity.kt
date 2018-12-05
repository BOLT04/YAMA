package isel.pt.yama.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.adapter.ChatAdapter
import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import isel.pt.yama.model.SentMessageMD
import isel.pt.yama.model.TeamMD


open class ChatActivity : AppCompatActivity() {

    //TODO: make view model for this activity that holds a list of  an object containing sent messageMD and avatar img of the currentUser who sent it!

    lateinit var repo : YAMARepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val app = getYAMAApplication()//TODO: is this a good solution? Should we override getApplication instead of making this extension?

        repo = app.repository

        val viewModel = getViewModel("chat view model"){ //TODO extract to field
            ChatViewModel(app)
        }


        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true

        messagesList.layoutManager = layoutManager
        messagesList.setHasFixedSize(true)

        val adapter = ChatAdapter(app, this, viewModel.chatLog)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                layoutManager.smoothScrollToPosition(messagesList, null, adapter.itemCount)
            }
        })
        messagesList.adapter = adapter

        sendBtn.setOnClickListener {
            val msg = userMessageTxt.text
            if (msg.isEmpty())
                return@setOnClickListener

            val sentMsg = SentMessageMD(app.repository.currentUser!!, msg.toString(), Date())
            viewModel.sendMessage(sentMsg)
            userMessageTxt.text.clear()
        }

        /*
        //TODO
        viewModel.chatLog.observe(this, Observer<List<MessageDto>> {
            val newAdapter = ChatAdapter(app, this, viewModel.chatLog)
            newAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    layoutManager.smoothScrollToPosition(messagesList, null, newAdapter.itemCount)
                }
            })
            messagesList.adapter = newAdapter
            newAdapter.notifyDataSetChanged()

        })*/

        chatName.setOnClickListener{
            val intent = Intent(this, MembersActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onStart() {
        super.onStart()
        Log.d(getString(R.string.TAG), "Started :: "+this.localClassName.toString())
    }

    override fun onStop() {
        super.onStop()
        Log.d(getString(R.string.TAG), "Stopped :: "+this.localClassName.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(getString(R.string.TAG), "Destroyed :: "+this.localClassName.toString())
    }

}
