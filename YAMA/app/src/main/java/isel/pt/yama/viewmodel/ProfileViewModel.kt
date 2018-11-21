package isel.pt.yama.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication

class ProfileViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    val userAvatarImage: MutableLiveData<Bitmap> = MutableLiveData()

}