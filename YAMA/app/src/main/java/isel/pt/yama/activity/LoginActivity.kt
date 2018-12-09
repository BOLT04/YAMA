package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
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
                if (keep_login_checkbox.isChecked)
                    saveSharedPreferences(sharedPref)

                fillRepositoryInfo(app, viewModel)



                val intent = Intent(this, HomeActivity::class.java)
                app.repository.currentUser=viewModel.userInfo.value

                app.chatBoard.start()

                startActivity(intent)
            } else {
                Toast.makeText(application, R.string.error_login, Toast.LENGTH_LONG).show()
            }
        })

        login_btn.setOnClickListener {
            saveUserInputsToModel(viewModel)
            viewModel.submitLogin()
        }

        keep_login_checkbox.setOnClickListener {
            if (login_userID.text.isNullOrBlank()
                || login_orgID.text.isNullOrBlank()
                || login_personalToken.text.isNullOrBlank())
                keep_login_checkbox.isChecked = false
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


    private fun fillRepositoryInfo(app: YAMAApplication, model: LoginViewModel) {
        app.repository.currentUser = model.userInfo.value
        app.repository.organizationID = model.textOrganization
        app.repository.token = model.textToken
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
