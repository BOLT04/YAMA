package isel.pt.yama.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import isel.pt.yama.R
import isel.pt.yama.dto.UserDto
import isel.pt.yama.kotlinx.getYAMAApplication
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val app = getYAMAApplication()

        /*
        val model = this.getViewModel("somerandomkey"){ // TODO necessary?
            LoginViewModel(this.application as YAMAApplication)
        }*/

        val user = intent.getParcelableExtra(USER_EXTRA) as UserDto

        user_profile_login.text = user.login
        user_profile_name.text = user.name
        user_profile_email.text = user.email
        user_profile_followers.text = user.followers.toString()
        user_profile_userAvatar.setImageURI(Uri.parse(user.avatar_url))
    }
}
