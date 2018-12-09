package isel.pt.yama.common

import isel.pt.yama.activity.MainActivity


// name of preferences file
val SP_NAME = MainActivity::class.java.`package`.name + "_Preferences"

const val VIEW_MODEL_KEY = "Login view model key"

//INTENTS
const val PRIVATE_PROFILE = "PrivateProfile"

const val TEAM_NOTIFICATION_CHANNEL_ID: String = "TeamNotificationChannelId"
const val DB_UPDATE_JOB_ID : String = "DB_UPDATE_JOB_ID"
const val NOTIFICATION_ID = 100012