package isel.pt.yama.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.VolleyError
import isel.pt.yama.YAMAApplication

class UpdateTeamMembersWorker(context : Context, params : WorkerParameters)
    : Worker(context, params), UpdateWorkers {

    override fun doWork(): Result {
        return try {
            val size = inputData.getInt("0", 0)
            val app = applicationContext as YAMAApplication
            Log.v(app.TAG, "Worker is updating local DB with teams")
            for (i in 1..size) {
                app.repository.syncGetTeamMembers(app, app.repository.token, inputData.getInt("$i",0))
            }
            sendNotification(app) //TODO: do we need a notification for this
            Result.SUCCESS
        } catch (error: VolleyError) {
            if (canRecover(error)) Result.RETRY else Result.FAILURE
        }
    }
}