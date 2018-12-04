package isel.pt.yama.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.ReceivedMessage
import java.text.SimpleDateFormat
import isel.pt.yama.viewmodel.ChatViewModel
import java.util.Date
import androidx.lifecycle.MutableLiveData


abstract class ChatViewHolder( val context: LifecycleOwner, view: ViewGroup, var message: MutableLiveData<MessageDto>? = null) : RecyclerView.ViewHolder(view){
    val observer: Observer<MessageDto> = Observer { bindToView(it) }


    fun associateAndBind(message: MutableLiveData<MessageDto>?){
        this.message=message
        message?.observe(context, observer)
   
    }
    abstract fun bindToView(message: MessageDto?)
}

class ReceivedChatViewHolder(context: LifecycleOwner, view: ViewGroup) : ChatViewHolder(context, view) {

    private val avatarImgView: ImageView = view.findViewById(R.id.userAvatar)
    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val userNameView: TextView = view.findViewById(R.id.userName)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)


    override fun bindTo(message: MessageDto?) {
        val msg = message as ReceivedMessage
        if (msg.userAvatar != null)
            avatarImgView.setImageBitmap(msg.userAvatar)//make request Uri.parse(message?.user?.avatar_url))
        else
            Log.d(app.TAG, "avatar is null")

        sentMsgView.text = message.content
        userNameView.text= message.user//.name ?: message.user.login

        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(message.createdAt)

    override fun bindToView(message: MessageDto?) {
        //avatarImgView.setImageBitmap()//make request Uri.parse(message?.user?.avatar_url))

        sentMsgView.text = message?.content
        userNameView.text= message?.user?.name ?: message?.user?.login

        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(Date(message?.createdAt!!))

    }
}

class SentChatViewHolder(context: LifecycleOwner, view: ViewGroup) : ChatViewHolder(context, view) {

    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindToView(message: MessageDto? ) {
        sentMsgView.text = message?.content
        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(message?.createdAt!!)
    }
}

const val MESSAGE_RECEIVED_CODE = 1
const val MESSAGE_SENT_CODE =2

class ChatAdapter(val app: YAMAApplication,
                  viewModel: ChatViewModel) : RecyclerView.Adapter<ChatViewHolder>() {

    private val chatLog = viewModel.chatLog


    override fun getItemCount(): Int {
        Log.d(app.TAG, "getItemCount: size=" +  chatLog.value?.size)
        return chatLog.value?.size ?: 0
    }

    override fun getItemViewType(position: Int) =
            if(chatLog.value?.get(position) is ReceivedMessage)
                MESSAGE_RECEIVED_CODE
            else
                MESSAGE_SENT_CODE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
            if (viewType == MESSAGE_RECEIVED_CODE)
                ReceivedChatViewHolder(context, LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_receive, parent, false) as ViewGroup)
            else
                SentChatViewHolder(context, LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_send, parent, false) as ViewGroup)

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {


        holder.associateAndBind(chatLog.value?.get(position))
    }

    override fun onViewRecycled(holder: ChatViewHolder) {
        holder.message?.removeObserver(holder.observer) //TODO de certeza que ja tem mensagem é alterar o message para livedata
        super.onViewRecycled(holder)
    }
}