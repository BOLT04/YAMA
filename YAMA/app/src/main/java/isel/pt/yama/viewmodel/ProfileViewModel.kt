package isel.pt.yama.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.model.UserMD

class ProfileViewModel(app : YAMAApplication, user : UserMD) : AndroidViewModel(app) {

    val userAvatarImage: MutableLiveData<Bitmap> = MutableLiveData()
    init {
        app.repository.getAvatarImageFromUrl(user.avatar_url){userAvatarImage.value=it}
    }



}