package isel.pt.yama.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.ReceivedMessage
import java.text.SimpleDateFormat
import java.util.Date
import android.app.Application
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.dataAccess.firebase.ChatBoard
import isel.pt.yama.viewmodel.ChatViewModel


abstract class ChatViewHolder( view: ViewGroup) : RecyclerView.ViewHolder(view){
    abstract fun bindTo(message: MessageDto?)
}

class ReceivedChatViewHolder(val app: YAMAApplication, view: ViewGroup) : ChatViewHolder(view) {

    private val avatarImgView: ImageView = view.findViewById(R.id.userAvatar)
    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val userNameView: TextView = view.findViewById(R.id.userName)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindTo(message: MessageDto?) {
        avatarImgView.setImageBitmap((message as ReceivedMessage).userAvatar)//make request Uri.parse(message?.user?.avatar_url))

        sentMsgView.text = message.content
        userNameView.text= message.user//.name ?: message.user.login

        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(message.createdAt)
    }
}

class SentChatViewHolder(view: ViewGroup) : ChatViewHolder(view) {

    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindTo(message: MessageDto?) {
        sentMsgView.text = message?.content
        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(message?.createdAt!!)
    }
}


const val MESSAGE_RECEIVED_CODE = 1
const val MESSAGE_SENT_CODE =2

class ChatAdapter(val app: YAMAApplication, context: LifecycleOwner,
                  val viewModel: ChatViewModel) : RecyclerView.Adapter<ChatViewHolder>() {

    private val chatBoard: ChatBoard = app.chatBoard
    private val chatLog = viewModel.chatLog

    init {

        chatBoard.content.observe(context, Observer {

            for (msg in it) {
                this.notifyItemInserted(chatBoard.content.value?.size!!)

/*                val currentPosition = chatLog.value?.size
                val currentMessage = list[currentPosition!!.minus(1)]
                if (currentMessage is ReceivedMessage)
                    app.repository.getAvatarImage(currentMessage.user.avatarUrl) {
                        currentMessage.userAvatar = it
                        this.notifyItemChanged(currentPosition)
                    }*/
            }
            //for (msg in it)
                //Log.v("YAMAApp", "${msg.user} : ${msg.content} : ${msg.createdAt}")
        })

        /*chatLog.observe(context, Observer<List<MessageDto>> { list ->
            //this.notifyItemChanged(chatLog.value?.size!!.minus(1))
            this.notifyItemInserted(chatLog.value?.size!!)

            val currentPosition =  chatLog.value?.size
            val currentMessage = list[currentPosition!!.minus(1)]
            if(currentMessage is ReceivedMessage)
                app.repository.getAvatarImage(currentMessage.user.avatarUrl) {
                    currentMessage.userAvatar=it
                    this.notifyItemChanged(currentPosition)
                }
        })*/

    }




    override fun getItemCount(): Int = chatLog.value?.size ?: 0
    override fun getItemViewType(position: Int) =
            if(chatLog.value?.get(position) is ReceivedMessage)
                MESSAGE_RECEIVED_CODE
            else
                MESSAGE_SENT_CODE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
            if (viewType == MESSAGE_RECEIVED_CODE)
                ReceivedChatViewHolder(app, LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_receive, parent, false) as ViewGroup)
            else
                SentChatViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_send, parent, false) as ViewGroup)


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bindTo(chatLog.value?.get(position))
    }
}