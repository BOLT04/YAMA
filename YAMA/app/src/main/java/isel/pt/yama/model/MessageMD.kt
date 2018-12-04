package isel.pt.yama.model

import isel.pt.yama.dataAccess.database.User
import java.util.*


open class MessageMD(val user: User,
                     val content: String,
                     val createdAt: Date)

class ReceivedMessageMD(user : User, content: String, createdAt: Date ):
        MessageMD(user, content, createdAt)

class SentMessageMD(user : User, content: String, createdAt: Date ):
        MessageMD(user, content, createdAt)