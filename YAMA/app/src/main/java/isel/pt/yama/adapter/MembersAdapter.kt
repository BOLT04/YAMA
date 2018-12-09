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
import com.android.volley.toolbox.NetworkImageView
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

        holder.bindToView(viewModel.members.value?.get(position), listener)
    }


    interface OnMemberClickListener {
        fun onMemberClick(user: UserMD?)
    }
}

class MembersViewHolder(val app: YAMAApplication,
                        val context: LifecycleOwner,
                        view: View,
                        var listener: MembersAdapter.OnMemberClickListener?=null)
    : RecyclerView.ViewHolder(view) {


    private val memberAvatar: NetworkImageView = view.findViewById(R.id.memberAvatar)
    private val memberName: TextView = view.findViewById(R.id.memberName)
    private val member : androidx.constraintlayout.widget.ConstraintLayout = view.findViewById(R.id.member_view)



    fun bindToView(user: UserMD?, listener: MembersAdapter.OnMemberClickListener) {
        Log.v("YAMA DEBUG", "user?.name: " + user?.login)

        memberAvatar.setImageUrl(user?.avatar_url!!, app.imageLoader)

        memberName.text = user.login

        member.setOnClickListener{listener.onMemberClick(user)}
    }

}