package isel.pt.yama.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.dto.UserDto
import isel.pt.yama.viewmodel.MembersViewModel

class MembersAdapter(private val viewModel: MembersViewModel,
                     private val listener: OnMemberClickListener) : RecyclerView.Adapter<MembersViewHolder>() {

    override fun getItemCount() = viewModel.members.value?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false) as View

        return MembersViewHolder(view)
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        Log.v("YAMA DEBUG", "viewModel.members.value?.size: " + viewModel.members.value?.size)
        holder.bindTo(viewModel.members.value?.get(position), listener)
    }

    interface OnMemberClickListener {
        fun onMemberClick(user: UserDto?)
    }
}

class MembersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val textViewMembers: TextView = view.findViewById(android.R.id.text1)

    fun bindTo(user: UserDto?, listener: MembersAdapter.OnMemberClickListener) {
        Log.v("YAMA DEBUG", "user?.name: " + user?.login)
        textViewMembers.text = user?.login
        itemView.setOnClickListener { listener.onMemberClick(user) }
    }
}