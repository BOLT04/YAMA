package isel.pt.yama

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class YAMAApplication : Application() {

    lateinit var queue: RequestQueue

    override fun onCreate() {
        super.onCreate()
        queue = Volley.newRequestQueue(this)
    }

}
