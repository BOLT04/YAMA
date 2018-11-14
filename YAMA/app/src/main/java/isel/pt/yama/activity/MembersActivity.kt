package isel.pt.yama.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import isel.pt.yama.R
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.MembersViewModel
import kotlinx.android.synthetic.main.activity_team_elements.*

// Represents the activity where all the elements/members of a team are listed.
class MembersActivity : AppCompatActivity() {
    val TAG = MembersActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_elements)

        val teamId = intent.getIntExtra("teamId", -1)//TODO: what to put on default value

        Log.v(TAG, "received $teamId")

        membersView.setHasFixedSize(true) // TODO: why do we use this and what does it do? Deeper understanding
        membersView.layoutManager = LinearLayoutManager(this)

        val app = getYAMAApplication()
        val viewModel = getViewModel(VIEW_MODEL_KEY){
            MembersViewModel(app)
        }
    }
}
