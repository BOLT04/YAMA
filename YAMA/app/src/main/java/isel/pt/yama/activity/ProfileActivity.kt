package isel.pt.yama.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import isel.pt.yama.R
import isel.pt.yama.common.PRIVATE_PROFILE
import isel.pt.yama.kotlinx.getYAMAApplication
import kotlinx.android.synthetic.main.activity_profile.*
import isel.pt.yama.YAMAApplication
import isel.pt.yama.repository.YAMARepository
import isel.pt.yama.repository.model.User


class ProfileActivity : AppCompatActivity() {

    lateinit var user: User
    lateinit var app: YAMAApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)



         app = getYAMAApplication()

        val privateProfile = intent.getBooleanExtra(PRIVATE_PROFILE, false)



        val repo = app.repository


        user = getUser(privateProfile, repo)


        // The responsibility of initializing currentUser property ia on MainActivity or LoginActivity
        // So when we're here, its guaranteed to be not null

        printUser(user, app)

        refresh_button.setOnClickListener{

            if(privateProfile)
                repo.updateCurrentUser(this::updateUser)
            else
                repo.updateOtherUser(this::updateUser)
        }


    }
    fun updateUser(user: User) {
        this.user = user
        printUser(user, app)
    }


    fun getUser(privateProfile: Boolean, repo: YAMARepository): User {
        return if (privateProfile)
            repo.currentUser!!
        else
            repo.otherUser!!
    }

    private fun printUser(user: User, app: YAMAApplication) {
        user_profile_login.text = user.login
        user_profile_name.text = user.name
        user_profile_email.text = user.email
        user_profile_followers.text = user.followers.toString()
        user_profile_following.text = user.following.toString()

        user_profile_userAvatar.setDefaultImageResId(R.drawable.green)
        user_profile_userAvatar.setErrorImageResId(R.drawable.red)

        user_profile_userAvatar.setImageUrl(user.avatar_url, app.imageLoader)
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
