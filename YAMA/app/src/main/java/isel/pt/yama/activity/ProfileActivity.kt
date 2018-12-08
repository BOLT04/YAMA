package isel.pt.yama.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import isel.pt.yama.R
import isel.pt.yama.common.PRIVATE_PROFILE
import isel.pt.yama.common.VIEW_MODEL_KEY
import isel.pt.yama.kotlinx.getViewModel
import isel.pt.yama.kotlinx.getYAMAApplication
import isel.pt.yama.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import com.android.volley.toolbox.Volley



class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val app = getYAMAApplication()

        val privateProfile = intent.getBooleanExtra(PRIVATE_PROFILE, false)


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


        val user =
                if(privateProfile)
                    app.repository.currentUser!!
                else
                    app.repository.otherUser!!


        val viewModel = getViewModel(VIEW_MODEL_KEY){
            ProfileViewModel(app, user)
        }

        // The responsibility of initializing currentUser property ia on MainActivity or LoginActivity
        // So when we're here, its guaranteed to be not null.


        user_profile_login.text = user.login
        user_profile_name.text = user.name
        user_profile_email.text = user.email
        user_profile_followers.text = user.followers.toString()
        user_profile_following.text = user.following.toString()
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


*/      user_profile_userAvatar.setDefaultImageResId(R.drawable.green)
        user_profile_userAvatar.setErrorImageResId(R.drawable.red)

        user_profile_userAvatar.setImageUrl(user.avatar_url, app.imageLoader)
/*
        viewModel.userAvatarImage.observe(this, Observer<Bitmap> {
            user_profile_userAvatar.setImageBitmap(it)
        })
*/
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
