package isel.pt.yama.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import isel.pt.yama.R
import isel.pt.yama.common.SP_NAME
import kotlinx.android.synthetic.main.activity_home2.*


class Home2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            R.id.logout -> {
                getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().clear().apply()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
