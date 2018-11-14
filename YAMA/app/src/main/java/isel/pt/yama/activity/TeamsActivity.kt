package isel.pt.yama.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.adapter.TeamsAdapter
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import kotlinx.android.synthetic.main.activity_teams.*
import isel.pt.yama.viewmodel.TeamsViewModel
import pt.isel.pdm.yama.model.Team

class TeamsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)

        teamsView.setHasFixedSize(true) // TODO: why do we use this and what does it do? Deeper understanding
        teamsView.layoutManager = LinearLayoutManager(this)

        val app = getYAMAApplication()
        val viewModel = getViewModel(VIEW_MODEL_KEY){
            TeamsViewModel(app)
        }

        teamsView.adapter = TeamsAdapter(viewModel)

        viewModel.teams.observe(this, Observer<List<Team>> {
            teamsView.adapter = TeamsAdapter(viewModel)
        })

        val sharedPref = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        //TODO: this code is always the same, get sharedPref, setup the key string, get a value from it associated to that key
        //TODO:so make a function for this?? extension maybe
        val orgIdStr = getString(R.string.organizationId)
        val userTokenStr = getString(R.string.userToken)

        val orgId = sharedPref.getString(orgIdStr, null)
        val userToken = sharedPref.getString(userTokenStr, null)

        viewModel.updateTeams(userToken, orgId)
    }
}
