package isel.pt.yama.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.adapter.ChatAdapter
import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.SentMessageMD
import isel.pt.yama.viewmodel.TeamChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.list_item_msg_receive.*
import java.util.*


class TeamChatActivity : AppCompatActivity() {

    lateinit var repo : YAMARepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val app = getYAMAApplication()

        repo = app.repository

        chatName.text = repo.team!!.name

        val viewModel = getViewModel("team chat view model"){
            TeamChatViewModel(app)
        }


        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true

        messagesList.layoutManager = layoutManager
        messagesList.setHasFixedSize(true)

        val adapter = ChatAdapter(app, this, viewModel.chatLog, true)

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



        viewModel.chatLog.observe(this, Observer<List<MutableLiveData<MessageMD>>> {

            val newAdapter = ChatAdapter(app, this, viewModel.chatLog, true)
            newAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    layoutManager.smoothScrollToPosition(messagesList, null, newAdapter.itemCount)
                }
            })
            messagesList.adapter = newAdapter
            newAdapter.notifyDataSetChanged()

        })

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
