package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.lifecycle.Observer
import isel.pt.yama.R
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

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
        } else {

            val app = getYAMAApplication()// TODO: is this a good solution? Should we override getApplication instead of making this extension?

            val viewModel = getViewModel(VIEW_MODEL_KEY){
                LoginViewModel(app)
            }

/*            val orgIdStr = getString(R.string.organizationId)
            val userTokenStr = getString(R.string.userToken)

            // Get values in shared preferences.
            val userId = sharedPref.getString(userIdStr, null)
            val orgId = sharedPref.getString(orgIdStr, null)
            val userToken = sharedPref.getString(userTokenStr, null)*/

            // Prepare to launch HomeActivity.

            restoreUserInputTexts(viewModel, sharedPref)
            viewModel.submitLogin()

            val loginObserver = Observer<Boolean> { loginIsOk ->
                if (loginIsOk) {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra(USER_EXTRA, viewModel.userInfo.value)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            viewModel.loginIsOk.observe(this, loginObserver)

        }
    }

    private fun restoreUserInputTexts(model: LoginViewModel, sharedPref: SharedPreferences) {
            model.textUser = sharedPref.getString(getString(R.string.userId), "")
            model.textOrganization = sharedPref.getString(getString(R.string.organizationId), "")
            model.textToken = sharedPref.getString(getString(R.string.userToken), "")
    }
}
