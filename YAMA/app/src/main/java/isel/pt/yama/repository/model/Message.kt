package isel.pt.yama.repository.model

import java.util.*


open class Message(val user: User,
                   val content: String,
                   val createdAt: Date)

class ReceivedMessage(user : User, content: String, createdAt: Date ):
        Message(user, content, createdAt)

class SentMessage(user : User, content: String, createdAt: Date, var sent : Boolean = false ):
        Message(user, content, createdAt)