package isel.pt.yama.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.dto.Message
import isel.pt.yama.viewmodel.ChatViewModel
import java.util.*

abstract class ChatViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view){
    abstract fun bindTo(message: Message?)
}

class ReceivedChatViewHolder(view: ViewGroup) : ChatViewHolder(view) {

    private val avatarImgView: ImageView = view.findViewById(R.id.userAvatar)
    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val userNameView: TextView = view.findViewById(R.id.userName)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindTo(message: Message?) {// TODO: does this have to be nullable?
        avatarImgView.setImageURI(Uri.parse(message?.user?.avatar_url))
        sentMsgView.text = message?.text
        userNameView.text=message?.user?.name
        dateTimeView.text= Date(message?.createdAt!!).toString() //TODO doubleBANG
    }
}

class SentChatViewHolder(view: ViewGroup) : ChatViewHolder(view) {

    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindTo(message: Message?) {// TODO: does this have to be nullable?
        sentMsgView.text = message?.text
        dateTimeView.text= Date(message?.createdAt!!).toString() //TODO doubleBANG
    }
}


const val MESSAGE_RECEIVED_CODE = 1
const val MESSAGE_SENT_CODE =2

class ChatAdapter(val viewModel: ChatViewModel, val teamId: Int) : RecyclerView.Adapter<ChatViewHolder>() {

    override fun getItemCount(): Int = 0//viewModel.messages.value?.size ?: 0

    override fun getItemViewType(position: Int) =
            if(viewModel.wasSent(teamId, position)!!) //TODO !! not good?
                MESSAGE_SENT_CODE
            else
                MESSAGE_RECEIVED_CODE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
            if (viewType == MESSAGE_RECEIVED_CODE)
                ReceivedChatViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_receive, parent, false) as ViewGroup)
            else
                ReceivedChatViewHolder(LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_send, parent, false) as ViewGroup)


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {//TODO: this
        holder.bindTo(viewModel.getSpecificMessage(position))//viewModel.chatsMap.value?.get(teamId)?.get(position))
    }
}