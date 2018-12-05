package isel.pt.yama.model

import isel.pt.yama.dataAccess.database.User
import java.util.*


open class MessageMD(val user: UserMD,
                     val content: String,
                     val createdAt: Date)

class ReceivedMessageMD(user : UserMD, content: String, createdAt: Date ):
        MessageMD(user, content, createdAt)

class SentMessageMD(user : UserMD, content: String, createdAt: Date ):
        MessageMD(user, content, createdAt)