package isel.pt.yama.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import isel.pt.yama.activity.MembersActivity
import isel.pt.yama.viewmodel.TeamsViewModel
import pt.isel.pdm.yama.model.Team

class TeamsAdapter(private val viewModel: TeamsViewModel) : RecyclerView.Adapter<TeamsAdapter.TeamsViewHolder>() {

    override fun getItemCount() = viewModel.teams.value?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsAdapter.TeamsViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false) as ViewGroup
        view.setOnClickListener {
            val intent = Intent(parent.context, MembersActivity::class.java)
            val teamIDView: TextView = it.findViewById(android.R.id.text2)

            intent.putExtra("teamId", teamIDView.text.toString().toInt())

            parent.context.startActivity(intent)
        }

        return TeamsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        holder.bindTo(viewModel.teams.value?.get(position))
    }

    class TeamsViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        private val teamNameView: TextView = view.findViewById(android.R.id.text1) // TODO: refactor these names: text1 e text2
        private val teamIDView: TextView = view.findViewById(android.R.id.text2)

        fun bindTo(team: Team?) {
            teamNameView.text = team?.name.toString()
            teamIDView.text = team?.id.toString()
        }
    }
}

