package isel.pt.yama.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.android.volley.VolleyError
import isel.pt.yama.YAMAApplication

class UpdatePeriodicWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams), UpdateWorkers {
    override fun doWork(): Result {

        val updateTeamsRequest = OneTimeWorkRequestBuilder<UpdateTeamsWorker>()
                .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiresCharging(true)
                        .build())
                .build()

        val updateTeamMembersRequest = OneTimeWorkRequestBuilder<UpdateTeamMembersWorker>()
                .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiresCharging(true)
                        .build())
                .build()

        return try {
            val app = applicationContext as YAMAApplication
            Log.d(app.TAG, "UpdatePeriodicWorker: Worker is updating local DB with teams and members")
            app.workManager
                    .beginWith(updateTeamsRequest)
                    .then(updateTeamMembersRequest)
                    .enqueue()
            sendNotification(app)
            Result.SUCCESS
        } catch (error: VolleyError) {
            if (canRecover(error)) Result.RETRY else Result.FAILURE
        }
    }

}