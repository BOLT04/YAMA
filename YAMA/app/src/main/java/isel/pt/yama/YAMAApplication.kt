package isel.pt.yama

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import isel.pt.yama.dto.UserDto

class YAMAApplication : Application() {

    lateinit var queue: RequestQueue
    var userLiveData: MutableLiveData<UserDto> = MutableLiveData()

    override fun onCreate() {
        super.onCreate()
        queue = Volley.newRequestQueue(this)
    }

}
