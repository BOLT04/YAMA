package isel.pt.yama.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.model.UserMD
import isel.pt.yama.viewmodel.MembersViewModel

class MembersAdapter(val app: YAMAApplication,
                     val context: LifecycleOwner,
                     private val viewModel: MembersViewModel,
                     private val listener: OnMemberClickListener) : RecyclerView.Adapter<MembersViewHolder>() {

    override fun getItemCount() = viewModel.members.value?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_member, parent, false) as View

        return MembersViewHolder(app, context, view)
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        Log.v("YAMA DEBUG", "viewModel.members.value?.size: " + viewModel.members.value?.size)

        holder.associateAndBind(viewModel.members.value?.get(position))
    }

    override fun onViewRecycled(holder: MembersViewHolder) {
        holder.userLd?.removeObserver(holder.observer) //TODO de certeza que ja tem mensagem é alterar o messageMD para livedata
        super.onViewRecycled(holder)
    }

    interface OnMemberClickListener {
        fun onMemberClick(user: UserMD?)
    }
}

class MembersViewHolder(val app: YAMAApplication,
                        val context: LifecycleOwner,
                        view: View, var userLd: MutableLiveData<User>? = null)
    : RecyclerView.ViewHolder(view) {

    val observer: Observer<User> = Observer { bindToView(it) }

    private val memberAvatar: ImageView = view.findViewById(R.id.memberAvatar)
    private val memberName: TextView = view.findViewById(R.id.memberName)


    fun associateAndBind(user: MutableLiveData<User>?){
        this.userLd = user
        user?.observe(context, observer)
        bindToView(user?.value)
    }

    fun bindToView(user: User?) {
        Log.v("YAMA DEBUG", "user?.name: " + user?.login)


        memberAvatar.setImageBitmap(
                app.repository.getAvatarImageFromUrlSync(user?.avatarUrl!!)
        )

        memberName.text = user.login
    }

    fun bindToView(user: User?, listener: MembersAdapter.OnMemberClickListener) {
        bindToView(user)
        itemView.setOnClickListener { listener.onMemberClick(user) }
    }
}