package isel.pt.yama

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.firebase.FirebaseApp
import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.dataAccess.database.YAMADatabase
import isel.pt.yama.dataAccess.github.GithubApi
import isel.pt.yama.dataAccess.firebase.FirebaseDatabase


class YAMAApplication : Application() {
    val TAG = "YAMAApplication"
    lateinit var queue: RequestQueue
    lateinit var repository: YAMARepository
        private set

    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "onCreate application")

        queue = Volley.newRequestQueue(this)

        var localDb =
                //Room.databaseBuilder(this, YAMARoomDatabase::class.java, "YAMA_db")
                       // Room.inMemoryDatabaseBuilder(this, YAMADatabase::class.java)
                       // .build()
         Room.databaseBuilder(this, YAMADatabase::class.java, "YAMA_db").build()


        FirebaseApp.initializeApp(this)

        val chatBoard = Chat(this)
        //PopulateDbAsync(wordRoomDatabase).execute()

        repository = YAMARepository(this, GithubApi(this), localDb, FirebaseDatabase(chatBoard))
    }
}
