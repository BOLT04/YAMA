package isel.pt.yama.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import isel.pt.yama.R
import isel.pt.yama.dto.UserDto
import isel.pt.yama.kotlinx.getYAMAApplication
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
       /* setSupportActionBar(toolbar)

        // Setup menu icon on action bar.
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }*/

        // Check how this activity was created (through Login or Main activity).

        teamsBtn.setOnClickListener {
            val intent = Intent(this, TeamsActivity::class.java)
            startActivity(intent)
        }

        navView.setNavigationItemSelectedListener {
            it.isChecked = true // highlight item selected
            drawerLayout.closeDrawers()

            when (it.itemId) {
                R.id.nav_profile -> {
                    // TODO: refactor this code: either put it in Repository or a simple util function that removes redundancy
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                //R.id.nav_team -> //TODO: this

                //R.id.nav_options -> //TODO: this
            }

            true
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
