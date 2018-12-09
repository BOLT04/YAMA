package isel.pt.yama.worker

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.VolleyError
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.activity.TeamsActivity
import isel.pt.yama.common.NOTIFICATION_ID
import isel.pt.yama.common.TEAM_NOTIFICATION_CHANNEL_ID
import isel.pt.yama.model.TeamMD


interface UpdateWorkers {

    fun sendNotification(app: YAMAApplication) {

        val action = PendingIntent.getActivity(app, 101,
                TeamsActivity.createIntent(app, true), PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(app, TEAM_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(app.getString(R.string.teams_notification_title))
                .setContentText(app.getString(R.string.teams_notification_content))
                .setContentIntent(action)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        NotificationManagerCompat.from(app).notify(NOTIFICATION_ID, notification)
    }

    fun canRecover(error: VolleyError): Boolean {
        val statusCode =
                if (error.networkResponse != null) error.networkResponse.statusCode
                else 0
        return statusCode in 500..599
    }
}





