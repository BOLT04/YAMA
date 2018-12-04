package isel.pt.yama.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import isel.pt.yama.R
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val app = getYAMAApplication()

        val viewModel = getViewModel(VIEW_MODEL_KEY){
            ProfileViewModel(app)
        }

        // The responsibility of initializing user property ia on MainActivity or LoginActivity
        // So when we're here, its guaranteed to be not null.
        val user = app.repository.user!!

        user_profile_login.text = user.login
        user_profile_name.text = user.name
        user_profile_email.text = user.email
        user_profile_followers.text = user.followers.toString()

        viewModel.userAvatarImage.observe(this, Observer<Bitmap> {
            user_profile_userAvatar.setImageBitmap(it)
        })

        app.repository.getAvatarImageFromUrl(user.avatarUrl){viewModel.userAvatarImage.value=it}
        //app.repository.getAvatarImageFromUrl(user.login){viewModel.userAvatarImage.value=it} // TODO: avatarurl or login
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
