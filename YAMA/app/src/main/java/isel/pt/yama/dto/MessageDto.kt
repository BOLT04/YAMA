package isel.pt.yama.dto

import android.graphics.Bitmap
import isel.pt.yama.dataAccess.database.User
import java.util.*

const val USER_ID = "user_id"
const val CONTENT = "content"
const val CREATED_AT = "created_at"

open class MessageDto(val user: String = "",
                  val content : String = "",
                  val createdAt: Date = Date())

class ReceivedMessage(msg : MessageDto):
        MessageDto(msg.user, msg.content, msg.createdAt) {

    var userAvatar: Bitmap? = null
    constructor(msg: MessageDto, userAvatar: Bitmap?) : this(msg) {
        this.userAvatar = userAvatar
    }

}