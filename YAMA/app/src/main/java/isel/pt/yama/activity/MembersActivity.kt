package isel.pt.yama.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.adapter.MembersAdapter
import isel.pt.yama.common.SP_NAME
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dto.TeamDto
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.viewmodel.MembersViewModel
import kotlinx.android.synthetic.main.activity_members.*

// Represents the activity where all the elements/members of a team are listed.
class MembersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        val team: Team = intent.getParcelableExtra("team")//TODO: what to put on default value

        textViewTeamName.text = team.name
        textViewTeamDescription.text = team.description

        membersView.setHasFixedSize(true) // TODO: why do we use this and what does it do? Deeper understanding
        membersView.layoutManager = LinearLayoutManager(this)

        val app = getYAMAApplication()
        val viewModel = getViewModel(VIEW_MODEL_KEY){
            MembersViewModel(app)
        }

       // val intent = Intent(this, ChatActivity::class.java)

        val listener = object : MembersAdapter.OnMemberClickListener {
            override fun onMemberClick(user: User?) {
               //TODO: implement DM
                Toast.makeText(app, "Direct messaging comming soon", Toast.LENGTH_SHORT).show()
                // intent.putExtra("user", user)
                //startActivity(intent)
            }
        }

        membersView.adapter = MembersAdapter(app, this, viewModel, listener)

        viewModel.members.observe(this, Observer<List<MutableLiveData<User>>> {
            Log.v("YAMA DEBUG", "viewModel.members.size: " + viewModel.members.value?.size)
            membersView.adapter = MembersAdapter(app, this, viewModel, listener)
        })

        val sharedPref = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        //TODO: this code is always the same, get sharedPref, setup the key string, get a value from it associated to that key
        //TODO:so make a function for this?? extension maybe
        val orgIdStr = getString(R.string.organizationId)
        val userTokenStr = getString(R.string.userToken)

        val orgId = sharedPref.getString(orgIdStr, "")
        val userToken = sharedPref.getString(userTokenStr, "")

        viewModel.updateMembers(userToken, team.id, orgId)
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
