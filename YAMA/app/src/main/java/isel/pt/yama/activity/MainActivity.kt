package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import isel.pt.yama.R

// name of preferences file
val SP_NAME = MainActivity::class.java.`package`.name + "_Preferences"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

        val userIdStr = getString(R.string.userId)

        // We only need to check if one value exists
        // since we save them at the same time.
        if (!sharedPref.contains(userIdStr)) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()// TODO: is this correct?
        }

        val orgIdStr = getString(R.string.organizationId)
        val userTokenStr = getString(R.string.userToken)

        // Get values in shared preferences.
        val userId = sharedPref.getString(userIdStr, null)
        val orgId = sharedPref.getString(orgIdStr, null)
        val userToken = sharedPref.getString(userTokenStr, null)

        // Prepare to launch HomeActivity.
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(userIdStr, userId) //TODO: what are these used for?????
        intent.putExtra(orgIdStr, orgId)
        intent.putExtra(userTokenStr, userToken)

        startActivity(intent)
    }
}
