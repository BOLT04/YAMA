package isel.pt.yama.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.VolleyError
import isel.pt.yama.YAMAApplication

class UpdateTeamsWorker(context : Context, params : WorkerParameters)
    : Worker(context, params), UpdateWorkers {

    override fun doWork(): Result {
        return try {
            val app = applicationContext as YAMAApplication
            Log.v(app.TAG, "Worker is updating local DB with teams")
            val teams = app.repository.syncGetTeams(app, app.repository.token, app.repository.organizationID)
            for (team in teams) {
                var i = 1
                outputData = Data.Builder()
                        .putInt("0", teams.size)
                        .putAll(teams.map { it -> "${i++}" to it.id }.toMap())
                        .build()
            }
            sendNotification(app) //TODO: do we need a notification for this
            Result.SUCCESS
        } catch (error: VolleyError) {
            if (canRecover(error)) Result.RETRY else Result.FAILURE
        }
    }


}