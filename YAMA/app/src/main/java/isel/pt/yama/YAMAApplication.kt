package isel.pt.yama

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import isel.pt.yama.model.dataAccess.github.GithubApi
import isel.pt.yama.model.dataAccess.YAMARepository
import isel.pt.yama.model.dataAccess.database.YAMADatabase

class YAMAApplication : Application() {
    val TAG = "YAMAApplication"
    lateinit var queue: RequestQueue
    lateinit var repository: YAMARepository

    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "onCreate application")

        queue = Volley.newRequestQueue(this)

        var db =
                //Room.databaseBuilder(this, YAMARoomDatabase::class.java, "YAMA_db")
                        Room.inMemoryDatabaseBuilder(this, YAMADatabase::class.java)
                        .build()

        //PopulateDbAsync(wordRoomDatabase).execute()

        repository = YAMARepository(this, GithubApi(this), db) //TODO: is 'this' correct here?
    }
}
