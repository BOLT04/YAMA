package isel.pt.yama.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import isel.pt.yama.*
import isel.pt.yama.dto.UserDto
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import kotlinx.android.synthetic.main.activity_login.*

const val VIEW_MODEL_KEY = "YAMA_KEY"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val app = getYAMAApplication()

        val model = this.getViewModel(VIEW_MODEL_KEY){ // TODO necessary key?
            YAMAViewModel(app)
        }

        login_btn.setOnClickListener{

            model.makeRequest(
                    userID = login_userID.text.toString(),
                    orgID = login_orgID.text.toString(),
                    userToken = login_personalToken.text.toString()
            )

            app.userLiveData.observe(this, Observer<UserDto> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            })
        }
    }
}
