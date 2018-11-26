package isel.pt.yama

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import isel.pt.yama.model.GithubApi
import isel.pt.yama.model.YAMARepository

class YAMAApplication : Application() {
    val TAG: String = "YAMAApplication"
    lateinit var queue: RequestQueue
    lateinit var repository: YAMARepository

    override fun onCreate() {
        super.onCreate()
        queue = Volley.newRequestQueue(this)
        repository = YAMARepository(this, GithubApi(this)) //TODO: is 'this' correct here?
    }
}
