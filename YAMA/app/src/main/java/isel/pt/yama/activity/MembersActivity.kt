package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.adapter.MembersAdapter
import isel.pt.yama.adapter.TeamsAdapter
import isel.pt.yama.dto.Team
import isel.pt.yama.dto.UserDto
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.MembersViewModel
import isel.pt.yama.viewmodel.TeamsViewModel
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_teams.*

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
            override fun onMemberClick(user: UserDto?) {
               //TODO: implement DM
                Toast.makeText(app, "Direct messaging comming soon", Toast.LENGTH_SHORT).show()
                // intent.putExtra("user", user)
                //startActivity(intent)
            }
        }

        membersView.adapter = MembersAdapter(viewModel, listener)

        viewModel.members.observe(this, Observer<List<UserDto>> {
            Log.v("YAMA DEBUG", "viewModel.members.size: " + viewModel.members.value?.size)
            membersView.adapter = MembersAdapter(viewModel, listener)
        })

        val sharedPref = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        //TODO: this code is always the same, get sharedPref, setup the key string, get a value from it associated to that key
        //TODO:so make a function for this?? extension maybe
        val userTokenStr = getString(R.string.userToken)
        val userToken = sharedPref.getString(userTokenStr, "")

        viewModel.updateMembers(userToken, team.id)
    }
}
