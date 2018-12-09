package isel.pt.yama.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import isel.pt.yama.R
import isel.pt.yama.common.PRIVATE_PROFILE
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.model.UserMD


class ProfileActivity : AppCompatActivity() {

    lateinit var user: UserMD
    lateinit var app: YAMAApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        /*
        /*val customTitleSupported =
                requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        if (customTitleSupported) {*/
            window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                    R.layout.titlebar)
        //}

*/
         app = getYAMAApplication()

        val privateProfile = intent.getBooleanExtra(PRIVATE_PROFILE, false)


        //title = getString(profile)
/*
        val mRequestQueue = Volley.newRequestQueue(this)
        val imageLoader = ImageLoader(mRequestQueue, object : ImageLoader.ImageCache{

            val hashMap = HashMap<String,Bitmap>()

            override fun getBitmap(url: String?): Bitmap {
                return hashMap
            }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
*/

        val repo = app.repository


        user = getUser(privateProfile, repo)


        val viewModel = getViewModel(VIEW_MODEL_KEY){
            ProfileViewModel(app, user)
        }

        // The responsibility of initializing currentUser property ia on MainActivity or LoginActivity
        // So when we're here, its guaranteed to be not null

        printUser(user, app)

        /*findViewById<NetworkImageView>(R.id.titlebar_image)
                .setImageUrl(user.avatar_url, app.imageLoader)
        findViewById<TextView>(R.id.titlebar_title).text = user.name ?: user.login*/


/*
        app.imageLoader.get(user.avatar_url, ImageLoader.getImageListener(user_profile_userAvatar,
                R.drawable.ic_person_black_24dp, android.R.drawable.ic_dialog_alert));
        *//*
        app.imageLoader.get(user.avatar_url, object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer, isImmediate: Boolean) {

                user_profile_userAvatar.setImageBitmap(response.bitmap ?: BitmapFactory.decodeResource(resources, R.drawable.red))
                Log.v("TAG", response.toString())
            }

            override fun onErrorResponse(error: VolleyError) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        })

*/

        refresh_button.setOnClickListener{

            if(privateProfile)
                repo.updateCurrentUser(this::updateUser)
            else
                repo.updateOtherUser(this::updateUser)
        }


    }
    fun updateUser(user: UserMD) {
        this.user = user
        printUser(user, app)
    }


    fun getUser(privateProfile: Boolean, repo: YAMARepository): UserMD {
        return if (privateProfile)
            repo.currentUser!!
        else
            repo.otherUser!!
    }

    private fun printUser(user: UserMD, app: YAMAApplication) {
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
