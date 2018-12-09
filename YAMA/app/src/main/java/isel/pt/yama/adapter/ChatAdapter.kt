package isel.pt.yama.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.model.Message
import isel.pt.yama.repository.model.ReceivedMessage
import isel.pt.yama.repository.model.SentMessage
import java.text.SimpleDateFormat


abstract class ChatViewHolder(val context: LifecycleOwner, view: ViewGroup,
                              var message: MutableLiveData<Message>? = null)
    : RecyclerView.ViewHolder(view){

    val observer: Observer<Message> = Observer { bindToView(it) }

    fun associateAndBind(message: MutableLiveData<Message>?){
        this.message=message
        message?.observe(context, observer)
        bindToView(message?.value)
   
    }
    abstract fun bindToView(message: Message?)
}

class ReceivedChatViewHolder(val imageLoader: ImageLoader, context: LifecycleOwner, view: ViewGroup) : ChatViewHolder(context, view) {

    private val avatarImgView: NetworkImageView = view.findViewById(R.id.userAvatar)
    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val userNameView: TextView = view.findViewById(R.id.userName)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)


    override fun bindToView(message: Message?) {
        avatarImgView.setImageUrl(message?.user?.avatar_url!!, imageLoader)

        sentMsgView.text = message.content
        userNameView.text= message.user.name ?: message.user.login

        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(message.createdAt)
    }
}

class SentChatViewHolder(val repo: YAMARepository, context: LifecycleOwner, view: ViewGroup) : ChatViewHolder(context, view) {

    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindToView(message: Message? ) {
        iconImageView.setImageResource(
                if((message as SentMessage).sent)
                    R.drawable.green
                else
                    R.drawable.red
        )
        sentMsgView.text = message.content
        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(message.createdAt)
    }
}

const val MESSAGE_RECEIVED_CODE = 1
const val MESSAGE_SENT_CODE =2

class ChatAdapter(val app: YAMAApplication,
                  val context: LifecycleOwner,
                  val chatLog: MutableLiveData<List<MutableLiveData<Message>>>,
                  val teamChat : Boolean)
    : RecyclerView.Adapter<ChatViewHolder>() {

    init {
        chatLog.observe(context, Observer<List<MutableLiveData<Message>>> {
            this.notifyItemInserted(chatLog.value?.size!!)
        })
    }

    override fun getItemCount(): Int {
       // Log.d(app.TAG, "getItemCount: size=" +  chatLog.value?.size)
        return chatLog.value?.size ?: 0
    }

    override fun getItemViewType(position: Int) =
            if(chatLog.value?.get(position)?.value is ReceivedMessage)
                MESSAGE_RECEIVED_CODE
            else
                MESSAGE_SENT_CODE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
            if (viewType == MESSAGE_RECEIVED_CODE){
                val viewGroup = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_receive, parent, false) as ViewGroup

                viewGroup.findViewById<TextView>(R.id.userName).visibility =
                        if(teamChat) View.VISIBLE else View.GONE


                ReceivedChatViewHolder(app.imageLoader, context, viewGroup)
            }
            else
                SentChatViewHolder(app.repository, context, LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.list_item_msg_send, parent, false) as ViewGroup)

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        holder.associateAndBind(chatLog.value?.get(position))
    }

    override fun onViewRecycled(holder: ChatViewHolder) {
        holder.message?.removeObserver(holder.observer)
        super.onViewRecycled(holder)
    }
}