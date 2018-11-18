package isel.pt.yama.viewmodel

import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import isel.pt.yama.R
import isel.pt.yama.Repository
import isel.pt.yama.YAMAApplication
import isel.pt.yama.network.GetRequestImage

class ProfileViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    val userAvatarImage: MutableLiveData<Bitmap> = MutableLiveData()

    fun getAvatarImage(url: String) {
    //    app.repository.getAvatarImage(url)

    }

}