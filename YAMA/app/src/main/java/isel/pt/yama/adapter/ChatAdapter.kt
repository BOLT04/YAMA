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
import isel.pt.yama.dto.SentMessage
import java.text.SimpleDateFormat
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
        dateTimeView.text= sdf.format(Date(message?.createdAt!!))
    }
}


const val MESSAGE_RECEIVED_CODE = 1
const val MESSAGE_SENT_CODE =2

class ChatAdapter(val app: YAMAApplication, val context: LifecycleOwner,
                  val chatLog: LiveData<List<MutableLiveData<MessageDto>>>) : RecyclerView.Adapter<ChatViewHolder>() {

    init {
        chatLog.observe(context, Observer<List<MutableLiveData<MessageDto>>> { list ->
            //this.notifyItemChanged(chatLog.value?.size!!.minus(1))
            this.notifyItemInserted(chatLog.value?.size!!)

            val currentPosition =  chatLog.value?.size
            val currentMessage = list[currentPosition!!.minus(1)].value
            if(currentMessage is ReceivedMessage)
                app.repository.getAvatarImage(currentMessage.user.avatarUrl) {
                    currentMessage.userAvatar=it
                    this.notifyItemChanged(currentPosition)
                }
        })

    }


    override fun getItemCount(): Int = chatLog.value?.size ?: 0
    override fun getItemViewType(position: Int) =
            if(chatLog.value?.get(position)?.value is SentMessage)
                MESSAGE_SENT_CODE
            else
                MESSAGE_RECEIVED_CODE

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