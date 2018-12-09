package isel.pt.yama.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserAssociation

class HomeAdapter(private val teamsLv: MutableLiveData<List<TeamMD>>,
                  private val usersLv: MutableLiveData<List<UserAssociation>>,
                  val context: LifecycleOwner,
                  private val teamListener: OnTeamClickListener,
                  private val userListener: OnUserAssClickListener) : RecyclerView.Adapter<HomeViewHolder>() {

    var list :MutableList<Any> = mutableListOf()
    init {
       /* teamsLv.observe(context, Observer<List<TeamMD>> { teams ->
            //this.notifyItemChanged(chatLog.value?.size!!.minus(1))
            this.notifyItemInserted(teamsLv.value?.size!!)
        })

        usersLv.observe(context, Observer<List<UserMD>> { users ->
            //this.notifyItemChanged(chatLog.value?.size!!.minus(1))
            this.notifyItemInserted(usersLv.value?.size!!)
        })*/

        list.addAll(usersLv.value ?: mutableListOf())
        list.addAll(teamsLv.value ?: mutableListOf())


    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false) as View

        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val curr = list.get(position)

        if(curr is TeamMD)
            holder.bindTo(curr, teamListener)
        else if(curr is UserAssociation)
            holder.bindTo(curr, userListener)


    }

    interface OnTeamClickListener {
        fun onTeamClick(team: TeamMD?)
    }

    interface OnUserAssClickListener {
        fun onUserAssClick(userAss: UserAssociation?)
    }
}

class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(android.R.id.text1)

    fun bindTo(team: TeamMD?, listener: HomeAdapter.OnTeamClickListener) {
        name.text = team?.name
        itemView.setOnClickListener { listener.onTeamClick(team) }
    }

    fun bindTo(userAss: UserAssociation?, listener: HomeAdapter.OnUserAssClickListener) {
        val user = userAss?.user
        name.text = user?.name ?: user?.login
        itemView.setOnClickListener { listener.onUserAssClick(userAss) }
    }
}