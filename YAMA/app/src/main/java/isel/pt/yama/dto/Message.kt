package isel.pt.yama.dto

import android.graphics.Bitmap

abstract class Message( val user: UserDto,
                        val text : String,
                        val createdAt: Long)

class ReceivedMessage(user: UserDto, text: String, createdAt: Long):
        Message(user = user, text = text, createdAt = createdAt){

    var userAvatar: Bitmap? = null
    constructor(user: UserDto, text: String, createdAt: Long, userAvatar: Bitmap):
           this(user, text, createdAt){
        this.userAvatar=userAvatar
    }

}

class SentMessage(user: UserDto, text: String, createdAt: Long):
        Message(user = user, text = text, createdAt = createdAt )
