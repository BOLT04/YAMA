package isel.pt.yama.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import java.text.SimpleDateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.ReceivedMessageMD


abstract class ChatViewHolder(val context: LifecycleOwner, view: ViewGroup,
                              var messageMD: MutableLiveData<MessageMD>? = null)
    : RecyclerView.ViewHolder(view){

    val observer: Observer<MessageMD> = Observer { bindToView(it) }
//TODO a barbara tem ideia de ter lido ou

    fun associateAndBind(messageMD: MutableLiveData<MessageMD>?){
        this.messageMD=messageMD
        messageMD?.observe(context, observer)
        bindToView(messageMD?.value)
   
    }
    abstract fun bindToView(messageMD: MessageMD?)
}

class ReceivedChatViewHolder(val app: YAMAApplication, context: LifecycleOwner, view: ViewGroup) : ChatViewHolder(context, view) {

    private val avatarImgView: ImageView = view.findViewById(R.id.userAvatar)
    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val userNameView: TextView = view.findViewById(R.id.userName)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)
/*

    override fun bindTo(messageMD: MessageDto?) {
        val msg = messageMD as ReceivedMessageMD
        if (msg.userAvatar != null)
            avatarImgView.setImageBitmap(msg.userAvatar)//make request Uri.parse(messageMD?.user?.avatar_url))
        // else
        // Log.d(TAG, "avatar is null")

        sentMsgView.text = messageMD.content
        userNameView.text = messageMD.user//.name ?: messageMD.user.login

        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text = sdf.format(messageMD.createdAt)
    }
    */

    override fun bindToView(messageMD: MessageMD?) {
        avatarImgView.setImageBitmap(
                app.repository.getAvatarImageFromUrlSync(messageMD?.user?.avatarUrl!!)
        )//make request Uri.parse(messageMD?.user?.avatar_url))

        sentMsgView.text = messageMD?.content
        userNameView.text= messageMD?.user?.name ?: messageMD?.user?.login

        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(messageMD?.createdAt)

    }
}

class SentChatViewHolder(context: LifecycleOwner, view: ViewGroup) : ChatViewHolder(context, view) {

    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindToView(messageMD: MessageMD? ) {
        sentMsgView.text = messageMD?.content
        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(messageMD?.createdAt!!)
    }
}

const val MESSAGE_RECEIVED_CODE = 1
const val MESSAGE_SENT_CODE =2

class ChatAdapter(val app: YAMAApplication,
                  val context: LifecycleOwner,
                  val chatLog: MutableLiveData<List<MutableLiveData<MessageMD>>>)
    : RecyclerView.Adapter<ChatViewHolder>() {

    init {
        chatLog.observe(context, Observer<List<MutableLiveData<MessageMD>>> { list ->
            //this.notifyItemChanged(chatLog.value?.size!!.minus(1))
            this.notifyItemInserted(chatLog.value?.size!!)

            /*
            val currentPosition =  chatLog.value?.size
            val currentMessage = list[currentPosition!!.minus(1)].value
            if(currentMessage is ReceivedMessageMD)
                app.repository.getAvatarImage(currentMessage.user.avatarUrl) {
                    currentMessage.userAvatar=it
                    this.notifyItemChanged(currentPosition)
                }
                */
        })

    }

    override fun getItemCount(): Int {
        Log.d(app.TAG, "getItemCount: size=" +  chatLog.value?.size)
        return chatLog.value?.size ?: 0
    }

    override fun getItemViewType(position: Int) =
            if(chatLog.value?.get(position)?.value is ReceivedMessageMD)
                MESSAGE_RECEIVED_CODE
            else
                MESSAGE_SENT_CODE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
            if (viewType == MESSAGE_RECEIVED_CODE)
                ReceivedChatViewHolder(app,context, LayoutInflater
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
        holder.messageMD?.removeObserver(holder.observer) //TODO de certeza que ja tem mensagem é alterar o messageMD para livedata
        super.onViewRecycled(holder)
    }
}