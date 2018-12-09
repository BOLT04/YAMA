package isel.pt.yama.activity

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.adapter.HomeAdapter
import isel.pt.yama.common.DB_UPDATE_JOB_ID
import isel.pt.yama.common.SP_NAME
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.model.TeamMD
import isel.pt.yama.viewmodel.HomeViewModel
import isel.pt.yama.worker.UpdateTeamsWorker
import kotlinx.android.synthetic.main.activity_home2.*
import java.util.concurrent.TimeUnit


class Home2Activity : AppCompatActivity() {

    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)
        setSupportActionBar(toolbar)

        chatsView.setHasFixedSize(true)
        chatsView.layoutManager = LinearLayoutManager(this)

        val app = getYAMAApplication()
        viewModel = getViewModel(VIEW_MODEL_KEY){
            HomeViewModel(app)
        }

        val intent = Intent(this, ChatActivity::class.java)

        val listener = object : HomeAdapter.OnTeamClickListener {
            override fun onTeamClick(team: TeamMD?) {
                app.repository.team = team
                app.chatBoard.associateTeam(team!!)
                startActivity(intent)
            }
        }

        chatsView.adapter = HomeAdapter(viewModel.teams, this, listener)

        viewModel.teams.observe(this, Observer<List<TeamMD>> {
            chatsView.adapter = HomeAdapter(viewModel.teams, this, listener)
        })

        //viewModel.updateTeams()
        viewModel.updateChats()

        scheduleDBUpdate(app)
    }

    private fun scheduleDBUpdate(app : YAMAApplication) {

        val updateRequest = PeriodicWorkRequestBuilder<UpdateTeamsWorker>(
                15, TimeUnit.SECONDS)
                .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiresCharging(true)
                        .build())
                .build()

        app.workManager.enqueueUniquePeriodicWork(
                DB_UPDATE_JOB_ID,
                ExistingPeriodicWorkPolicy.KEEP,
                updateRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            R.id.logout -> {
                getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().clear().apply()
                finish()
                val intent = Intent(this, LoginActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
                true
            }
            R.id.teams -> {
                val intent = Intent(this, TeamsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    override fun onResume() {
        super.onResume()
        viewModel.updateTeams()
    }
}
