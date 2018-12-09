package isel.pt.yama.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import java.text.SimpleDateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.ReceivedMessageMD
import isel.pt.yama.model.SentMessageMD
import kotlinx.android.synthetic.main.list_item_msg_receive.*


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

class ReceivedChatViewHolder(val imageLoader: ImageLoader, context: LifecycleOwner, view: ViewGroup) : ChatViewHolder(context, view) {

    private val avatarImgView: NetworkImageView = view.findViewById(R.id.userAvatar)
    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val userNameView: TextView = view.findViewById(R.id.userName)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)
/*

    override fun bindTo(messageMD: MessageDto?) {
        val msg = messageMD as ReceivedMessageMD
        if (msg.userAvatar != null)
            avatarImgView.setImageBitmap(msg.userAvatar)//make request Uri.parse(messageMD?.currentUser?.avatar_url))
        // else
        // Log.d(TAG, "avatar is null")

        sentMsgView.text = messageMD.content
        userNameView.text = messageMD.currentUser//.name ?: messageMD.currentUser.login

        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text = sdf.format(messageMD.createdAt)
    }
    */

    override fun bindToView(messageMD: MessageMD?) {
        avatarImgView.setImageUrl(messageMD?.user?.avatar_url!!, imageLoader)

        sentMsgView.text = messageMD.content
        userNameView.text= messageMD.user.name ?: messageMD.user.login

        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(messageMD.createdAt)
    }
}

class SentChatViewHolder(val repo: YAMARepository, context: LifecycleOwner, view: ViewGroup) : ChatViewHolder(context, view) {

    private val sentMsgView: TextView = view.findViewById(R.id.message)
    private val iconImageView: ImageView = view.findViewById(R.id.iconImageView)
    private val dateTimeView: TextView = view.findViewById(R.id.dateTime)

    override fun bindToView(messageMD: MessageMD? ) {
        iconImageView.setImageResource(
                if((messageMD as SentMessageMD).sent)
                    R.drawable.green
                else
                    R.drawable.red
        )
        sentMsgView.text = messageMD.content
        val sdf = SimpleDateFormat.getDateTimeInstance()
        dateTimeView.text= sdf.format(messageMD.createdAt)
    }
}

const val MESSAGE_RECEIVED_CODE = 1
const val MESSAGE_SENT_CODE =2

class ChatAdapter(val app: YAMAApplication,
                  val context: LifecycleOwner,
                  val chatLog: MutableLiveData<List<MutableLiveData<MessageMD>>>,
                  val teamChat : Boolean)
    : RecyclerView.Adapter<ChatViewHolder>() {

    init {
        chatLog.observe(context, Observer<List<MutableLiveData<MessageMD>>> {
            list ->
            //this.notifyItemChanged(chatLog.value?.size!!.minus(1))
            this.notifyItemInserted(chatLog.value?.size!!)

            /*
            val currentPosition =  chatLog.value?.size
            val currentMessage = list[currentPosition!!.minus(1)].value
            if(currentMessage is ReceivedMessageMD)
                repo.repository.getAvatarImage(currentMessage.currentUser.avatarUrl) {
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
        holder.messageMD?.removeObserver(holder.observer) //TODO de certeza que ja tem mensagem é alterar o messageMD para livedata
        super.onViewRecycled(holder)
    }
}