package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.adapter.MembersAdapter
import isel.pt.yama.common.SP_NAME
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.repository.model.Team
import isel.pt.yama.repository.model.User
import isel.pt.yama.viewmodel.MembersViewModel
import kotlinx.android.synthetic.main.activity_members.*

// Represents the activity where all the elements/members of a team are listed.
class MembersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        val app = getYAMAApplication()

        val team: Team = app.repository.team!!

        textViewTeamName.text = team.name
        textViewTeamDescription.text = team.description

        membersView.setHasFixedSize(true)
        membersView.layoutManager = LinearLayoutManager(this)


        val viewModel = getViewModel(VIEW_MODEL_KEY){
            MembersViewModel(app, app.repository)
        }

        val intent = Intent(this, UserChatActivity::class.java)

        val listener = object : MembersAdapter.OnMemberClickListener {
            override fun onMemberClick(user: User?) {

                if(app.repository.currentUser!=user){

                    app.repository.otherUser = user

                    app.chatBoard.associateUser(user?.login!!)

                    startActivity(intent)
                }
            }
        }

        membersView.adapter = MembersAdapter(app, this, viewModel, listener)

        viewModel.members.observe(this, Observer<List<User>> {
            Log.v("YAMA DEBUG", "viewModel.members.size: " + viewModel.members.value?.size)
            membersView.adapter = MembersAdapter(app, this, viewModel, listener)
        })

        val sharedPref = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

        val orgIdStr = getString(R.string.organizationId)
        val userTokenStr = getString(R.string.userToken)

        val orgId = sharedPref.getString(orgIdStr, "")!!
        val userToken = sharedPref.getString(userTokenStr, "")!!

        viewModel.updateMembers(userToken, orgId)
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
