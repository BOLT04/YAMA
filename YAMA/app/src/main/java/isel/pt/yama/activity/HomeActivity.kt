package isel.pt.yama.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.core.view.GravityCompat
import isel.pt.yama.R
import isel.pt.yama.dto.UserDto
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        // Setup menu icon on action bar.
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        // Check how this activity was created (through Login or Main activity).
        val userDto = intent.getParcelableExtra<UserDto>(USER_EXTRA)
        if (userDto != null) {
            // Then make the request to get the user object
            val userToken = intent.getStringExtra(getString(R.string.userToken))
            //TODO: MAKE request how? how to put in userDto the object from succesListener
        }

        navView.setNavigationItemSelectedListener {
            it.isChecked = true // highlight item selected
            drawerLayout.closeDrawers()

            when (it.itemId) {
                R.id.nav_profile -> {
                    // TODO: refactor this code: either put it in Repository or a simple util function that removes redundancy
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra(USER_EXTRA, userDto)

                    startActivity(intent)
                }
                //R.id.nav_team -> //TODO: this

                //R.id.nav_options -> //TODO: this
            }

            true //TODO: why dont we need return statement??
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}