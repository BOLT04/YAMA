package isel.pt.yama.dto

import android.graphics.Bitmap
import isel.pt.yama.dataAccess.database.User

abstract class MessageDto(val user: User,
                          val text : String,
                          val createdAt: Long)

class ReceivedMessage(user: User, text: String, createdAt: Long):
        MessageDto(user = user, text = text, createdAt = createdAt){

    var userAvatar: Bitmap? = null
    constructor(user: User, text: String, createdAt: Long, userAvatar: Bitmap):
           this(user, text, createdAt){
        this.userAvatar=userAvatar
    }

}

class SentMessage(user: User, text: String, createdAt: Long):
        MessageDto(user = user, text = text, createdAt = createdAt )
