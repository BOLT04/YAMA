package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.adapter.TeamsAdapter
import isel.pt.yama.adapter.TeamsAdapter.OnTeamClickListener
import isel.pt.yama.common.SP_NAME
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.dto.TeamDto
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.model.dataAccess.database.Team
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

        teamsView.setHasFixedSize(true) // TODO: why do we use this and what does it do? Deeper understanding
        teamsView.layoutManager = LinearLayoutManager(this)

        val app = getYAMAApplication()
        val viewModel = getViewModel(VIEW_MODEL_KEY){
            TeamsViewModel(app)
        }

        val intent = Intent(this, ChatActivity::class.java)

        val listener = object : OnTeamClickListener {
            override fun onTeamClick(team: Team?) {
                intent.putExtra("team", team)
                startActivity(intent)
            }
        }

        teamsView.adapter = TeamsAdapter(viewModel, listener)

        viewModel.teams.observe(this, Observer<List<Team>> {
            teamsView.adapter = TeamsAdapter(viewModel, listener)
        })

        val sharedPref = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        //TODO: this code is always the same, get sharedPref, setup the key string, get a value from it associated to that key
        //TODO:so make a function for this?? extension maybe
        val orgIdStr = getString(R.string.organizationId)
        val userTokenStr = getString(R.string.userToken)

        val orgId = sharedPref.getString(orgIdStr, "")
        val userToken = sharedPref.getString(userTokenStr, "")

        viewModel.updateTeams(userToken, orgId)
    }
}
