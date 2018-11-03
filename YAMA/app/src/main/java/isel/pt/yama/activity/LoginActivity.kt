package isel.pt.yama.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import isel.pt.yama.R
import isel.pt.yama.dto.UserDto
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

// TODO: where to define these global constants used in intent.get... and other places
const val VIEW_MODEL_KEY = "Login view model key"
const val USER_EXTRA = "UserDto"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val app = getYAMAApplication()// TODO: is this a good solution? Should we override getApplication instead of making this extension?

        val viewModel = getViewModel(VIEW_MODEL_KEY){
            LoginViewModel(app)
        }

        login_btn.setOnClickListener{
            viewModel.makeRequest(
                    //TODO: good idea to pass shared prefs to view model?
                    getSharedPreferences(SP_NAME, Context.MODE_PRIVATE),
                    userID = login_userID.text.toString(),
                    orgID = login_orgID.text.toString(),
                    userToken = login_personalToken.text.toString()
            )

            viewModel.userLiveData.observe(this, Observer<UserDto> {
                // If the request made above is successful, then launch HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra(USER_EXTRA, it)

                startActivity(intent)
            })
        }
    }
}
