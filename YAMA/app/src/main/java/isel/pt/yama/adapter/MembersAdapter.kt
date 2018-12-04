package isel.pt.yama.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.R
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.viewmodel.MembersViewModel

class MembersAdapter(private val viewModel: MembersViewModel,
                     private val listener: OnMemberClickListener) : RecyclerView.Adapter<MembersViewHolder>() {

    override fun getItemCount() = viewModel.members.value?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_member, parent, false) as View

        return MembersViewHolder(view)
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        Log.v("YAMA DEBUG", "viewModel.members.value?.size: " + viewModel.members.value?.size)
        holder.bindTo(viewModel.members.value?.get(position), listener)
    }

    interface OnMemberClickListener {
        fun onMemberClick(user: User?)
    }
}

class MembersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val memberAvatar: ImageView = view.findViewById(R.id.memberAvatar)
    private val memberName: TextView = view.findViewById(R.id.memberName)

    fun bindTo(user: User?, listener: MembersAdapter.OnMemberClickListener) {
        Log.v("YAMA DEBUG", "user?.name: " + user?.login)


        //memberAvatar.setImageBitmap(user.)
        memberName.text = user?.login
        itemView.setOnClickListener { listener.onMemberClick(user) }
    }
}