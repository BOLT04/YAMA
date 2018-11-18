package isel.pt.yama.activity

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

        val app = getYAMAApplication()// TODO: is this a good solution? Should we override getApplication instead of making this extension?
        val viewModel = getViewModel(VIEW_MODEL_KEY){
            TeamsViewModel(app)
        }

        teamsView.adapter = TeamsAdapter(viewModel)

        viewModel.teams.observe(this, Observer<List<Team>> {
            teamsView.adapter = TeamsAdapter(viewModel)
        })
    }
}
