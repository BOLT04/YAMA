package isel.pt.yama.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import isel.pt.yama.R
import isel.pt.yama.dto.UserDto
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val app = getYAMAApplication()// TODO: is this a good solution? Should we override getApplication instead of making this extension?

        val viewModel = getViewModel(VIEW_MODEL_KEY){
            ProfileViewModel(app)
        }

        val user = intent.getParcelableExtra(USER_EXTRA) as UserDto

        user_profile_login.text = user.login
        user_profile_name.text = user.name
        user_profile_email.text = user.email
        user_profile_followers.text = user.followers.toString()

        val imageObserver = Observer<Bitmap> {
            user_profile_userAvatar.setImageBitmap(it)
        }

        viewModel.userAvatarImage.observe(this, imageObserver )

        viewModel.getAvatarImage(user.avatar_url)
    }
}
