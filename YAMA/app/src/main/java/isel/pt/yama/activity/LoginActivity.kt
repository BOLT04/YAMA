package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import isel.pt.yama.R
import isel.pt.yama.common.SP_NAME
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val app = getYAMAApplication()

        val viewModel = getViewModel(VIEW_MODEL_KEY){
            LoginViewModel(app)
        }

        val sharedPref = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

        restoreUserInputTexts(viewModel, sharedPref)

        viewModel.loginIsOk.observe(this, Observer<Boolean> { loginIsOk ->
            if (loginIsOk) {
                saveSharedPreferences(sharedPref)
                val intent = Intent(this, HomeActivity::class.java)
                app.repository.user=viewModel.userInfo.value
                startActivity(intent)
            } else {
                Toast.makeText(application, R.string.error_login, Toast.LENGTH_LONG).show()
            }
        })

        login_btn.setOnClickListener {
            saveUserInputsToModel(viewModel)
            viewModel.submitLogin()
        }
    }

    private fun saveUserInputsToModel(model: LoginViewModel) {
        model.textUser = login_userID.text.toString()
        model.textOrganization = login_orgID.text.toString()
        model.textToken = login_personalToken.text.toString()
    }

    private fun saveSharedPreferences(sharedPref : SharedPreferences) {
        with (sharedPref.edit()) {
            putString(getString(R.string.userId), login_userID.text.toString())
            putString(getString(R.string.organizationId), login_orgID.text.toString())
            putString(getString(R.string.userToken), login_personalToken.text.toString())
            apply()
        }
    }

    private fun restoreUserInputTexts(model: LoginViewModel, sharedPref: SharedPreferences) {

        if (model.textOrganization == "")
            model.textUser = sharedPref.getString(getString(R.string.userId), "")

        if (model.textOrganization == "")
            model.textOrganization = sharedPref.getString(getString(R.string.organizationId), "")

        if (model.textToken == "")
            model.textToken = sharedPref.getString(getString(R.string.userToken), "")

        login_userID.text = Editable.Factory().newEditable(model.textUser)
        login_orgID.text = Editable.Factory().newEditable(model.textOrganization)
        login_personalToken.text = Editable.Factory().newEditable(model.textToken)
    }
}
