package isel.pt.yama.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.viewmodel.HomeViewModel

class HomeAdapter(private val teamsLv: MutableLiveData<List<Team>>,
                  val context: LifecycleOwner,
                  private val listener: OnTeamClickListener) : RecyclerView.Adapter<HomeViewHolder>() {

    init {
        teamsLv.observe(context, Observer<List<Team>> { teams ->
            //this.notifyItemChanged(chatLog.value?.size!!.minus(1))
            this.notifyItemInserted(teamsLv.value?.size!!)
        })

    }

    override fun getItemCount() = teamsLv.value?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false) as View

        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bindTo(teamsLv.value?.get(position), listener)
    }

    interface OnTeamClickListener {
        fun onTeamClick(team: Team?)
    }
}

class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val teamNameView: TextView = view.findViewById(android.R.id.text1)

    fun bindTo(team: Team?, listener: HomeAdapter.OnTeamClickListener) {
        teamNameView.text = team?.name
        itemView.setOnClickListener { listener.onTeamClick(team) }
    }
}