package isel.pt.yama

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.util.LruCache
import androidx.room.Room
import androidx.work.WorkManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.google.firebase.FirebaseApp
import isel.pt.yama.common.TEAM_NOTIFICATION_CHANNEL_ID
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
    lateinit var workManager: WorkManager

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
        workManager = WorkManager.getInstance()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {

        // Create notification channel if we are running on a O+ device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    TEAM_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.teams_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = getString(R.string.teams_channel_description)
            }

            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}
