package isel.pt.yama.dto

import android.graphics.Bitmap
import isel.pt.yama.dataAccess.database.User

const val USER_ID = "user_id"
const val CONTENT = "content"
const val CREATED_AT = "created_at"

abstract class MessageDto(val user: User,
                          val content : String,
                          val createdAt: Long){

    fun toMap(): Map<String, String> = mapOf(
            USER_ID to user.login, //TODO id or login?
            CONTENT to content,
            CREATED_AT to createdAt.toString()
    )

    fun fromMap(msg: Map<String, String>): MessageDto {
        MessageDto(msg.get(USER_ID), msg.get(CONTENT), msg.get(CREATED_AT))
    }
}


class ReceivedMessage(user: User, text: String, createdAt: Long):
        MessageDto(user = user, content = text, createdAt = createdAt){

    var userAvatar: Bitmap? = null
    constructor(user: User, text: String, createdAt: Long, userAvatar: Bitmap):
           this(user, text, createdAt){
        this.userAvatar=userAvatar
    }

}

class SentMessage(user: User, text: String, createdAt: Long):
        MessageDto(user = user, content = text, createdAt = createdAt )
