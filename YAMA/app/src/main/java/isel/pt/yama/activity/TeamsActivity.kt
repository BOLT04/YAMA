package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.adapter.TeamsAdapter
import isel.pt.yama.adapter.TeamsAdapter.OnTeamClickListener
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.repository.model.Team
import kotlinx.android.synthetic.main.activity_teams.*
import isel.pt.yama.viewmodel.TeamsViewModel

class TeamsActivity : AppCompatActivity() {

    companion object {
        val INITIAL_STATE_EXTRA_KEY = "SHOULD_DISPLAY"

        fun createIntent(origin: Context, shouldDisplay: Boolean) =
            Intent(origin, MainActivity::class.java).apply {
                if (shouldDisplay) putExtra(INITIAL_STATE_EXTRA_KEY, shouldDisplay)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)

        teamsView.setHasFixedSize(true)
        teamsView.layoutManager = LinearLayoutManager(this)

        val app = getYAMAApplication()
        val viewModel = getViewModel(VIEW_MODEL_KEY){
            TeamsViewModel(app, app.repository)
        }

        val intent = Intent(this, TeamChatActivity::class.java)

        val listener = object : OnTeamClickListener {
            override fun onTeamClick(team: Team?) {
                app.repository.team = team!!
                app.chatBoard.associateTeam(team)
                startActivity(intent)
            }
        }

        teamsView.adapter = TeamsAdapter(viewModel, listener)

        viewModel.teams.observe(this, Observer<List<Team>> {
            teamsView.adapter = TeamsAdapter(viewModel, listener)
        })


        viewModel.updateTeams()
    }

    override fun onStart() {
        super.onStart()
        Log.d(getString(R.string.TAG), "Started :: "+this.localClassName.toString())
    }

    override fun onStop() {
        super.onStop()
        Log.d(getString(R.string.TAG), "Stopped :: "+this.localClassName.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(getString(R.string.TAG), "Destroyed :: "+this.localClassName.toString())
    }



}
