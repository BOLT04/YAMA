package isel.pt.yama

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.dto.UserDto
import isel.pt.yama.viewmodel.LoginViewModel

class ChatViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {

    private val avatarImgView: ImageView = view.findViewById(R.id.avatarImg)
    private val sentMsgView: TextView = view.findViewById(R.id.sentMsg)

    fun bindTo(user: UserDto?) {// TODO: does this have to be nullable?
        avatarImgView.setImageURI(Uri.parse(user?.avatar_url))
        //sentMsgView.text = //TODO: this
    }
}

class ChatAdapter(val viewModel: LoginViewModel) : RecyclerView.Adapter<ChatViewHolder>() {

    override fun getItemCount(): Int = 0//viewModel.messages.value?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
            ChatViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_msg_send, parent, false) as ViewGroup
            )

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {//TODO: this
        //holder.bindTo(viewModel.currencies.value?.quotes?.get(position))
    }
}