package isel.pt.yama.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.viewmodel.TeamsViewModel
import pt.isel.pdm.yama.model.Team

class TeamsAdapter(private val viewModel: TeamsViewModel,
                   private val listener: OnTeamClickListener) : RecyclerView.Adapter<TeamsViewHolder>() {

    override fun getItemCount() = viewModel.teams.value?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false) as ViewGroup

        return TeamsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        holder.bindTo(viewModel.teams.value?.get(position), listener)
    }

    interface OnTeamClickListener {
        fun onItemClick(team: Team?)
    }
}
    class TeamsViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        private val teamNameView: TextView = view.findViewById(android.R.id.text1) // TODO: refactor these names: text1 e text2
        //private val teamIDView: TextView = view.findViewById(android.R.id.text2)

        fun bindTo(team: Team?, listener: TeamsAdapter.OnTeamClickListener) {
            teamNameView.text = team?.name
            //teamIDView.text = team?.id.toString()
            itemView.setOnClickListener { listener.onItemClick(team) }
        }
    }


