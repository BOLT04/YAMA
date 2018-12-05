package isel.pt.yama

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.google.firebase.FirebaseApp
import isel.pt.yama.dataAccess.YAMARepository
import isel.pt.yama.dataAccess.database.YAMADatabase
import isel.pt.yama.dataAccess.firebase.ChatBoard
import isel.pt.yama.dataAccess.github.GithubApi
import isel.pt.yama.dataAccess.firebase.FirebaseDatabase

class YAMAApplication : Application() {
    val TAG = "YAMAApplication"

    lateinit var queue: RequestQueue
    lateinit var repository: YAMARepository
    lateinit var chatBoard: ChatBoard
        private set

    lateinit var imageLoader: ImageLoader

    override fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks?) {
        Log.v("$TAG::actlog",callback?.javaClass.toString())
        Log.v("$TAG::actlog",callback?.toString())
    }


    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate application")

        queue = Volley.newRequestQueue(this)
        imageLoader = ImageLoader(queue,
                    object : ImageLoader.ImageCache {
                        private val cache = LruCache<String, Bitmap>(20)
                        override fun getBitmap(url: String): Bitmap {
                            return cache.get(url)
                        }
                        override fun putBitmap(url: String, bitmap: Bitmap) {
                            cache.put(url, bitmap)
                        }
                    })


        val localDb = Room.databaseBuilder(this, YAMADatabase::class.java, "YAMA_db").build()
        //Room.databaseBuilder(this, YAMARoomDatabase::class.java, "YAMA_db")
        // Room.inMemoryDatabaseBuilder(this, YAMADatabase::class.java)
        // .build()

        FirebaseApp.initializeApp(this)
        chatBoard = ChatBoard(this)
        //PopulateDbAsync(wordRoomDatabase).execute()

        repository = YAMARepository(this, GithubApi(this), localDb, FirebaseDatabase(chatBoard))
    }
}
