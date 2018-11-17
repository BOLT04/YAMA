package isel.pt.yama.adapter

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.R.id.messagesList
import isel.pt.yama.Repository
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.Message
import isel.pt.yama.dto.ReceivedMessage
import isel.pt.yama.dto.SentMessage
import isel.pt.yama.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date


abstract class ChatViewHolder( view: ViewGroup) : RecyclerView.ViewHolder(view){
    abstract fun bindTo(message: Message?)
}

class ReceivedChatViewHolder(val app: YAMAApplication, view: ViewGroup) : ChatViewHolder(view) {

    private val avatarImgView: ImageView = view.findViewById(R.id.userAvatar)
    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val userNameView: TextView = view.findViewById(R.id.userName)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindTo(message: Message?) {// TODO: does this have to be nullable?
        avatarImgView.setImageBitmap((message as ReceivedMessage).userAvatar)//make request Uri.parse(message?.user?.avatar_url))
        sentMsgView.text = message.text
        userNameView.text= message.user.name
        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(Date(message.createdAt))//message?.createdAt ? Date(message.createdAt.toString()) ?: "No date" //TODO doubleBANG
    }
}

class SentChatViewHolder(view: ViewGroup) : ChatViewHolder(view) {

    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindTo(message: Message?) {// TODO: does this have to be nullable?
        sentMsgView.text = message?.text
        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(Date(message?.createdAt!!)) //TODO doubleBANG
    }
}


const val MESSAGE_RECEIVED_CODE = 1
const val MESSAGE_SENT_CODE =2

class ChatAdapter(val app: YAMAApplication, context: LifecycleOwner ,val chatLog: LiveData<List<Message>>) : RecyclerView.Adapter<ChatViewHolder>() {

    init {
        chatLog.observe(context, Observer<List<Message>> { list ->
            this.notifyItemChanged(chatLog.value?.size!!.minus(1))
            val currentPostition =  chatLog.value?.size
            app.repository.getAvatarImage(list[currentPostition!!.minus(1)].user.avatar_url) {
                this.notifyItemChanged(currentPostition, it)
            }
        })

    }


    override fun getItemCount(): Int = chatLog.value?.size ?: 0
    override fun getItemViewType(position: Int) =
            if(chatLog.value?.get(position) is SentMessage) //TODO !! not good?
                MESSAGE_SENT_CODE
            else
                MESSAGE_RECEIVED_CODE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
            if (viewType == MESSAGE_RECEIVED_CODE)
                ReceivedChatViewHolder(app, LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_receive, parent, false) as ViewGroup)
            else
                SentChatViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_send, parent, false) as ViewGroup)


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {//TODO: this
        holder.bindTo(chatLog.value?.get(position))//viewModel.chatsMap.value?.get(teamId)?.get(position))
    }
}