package isel.pt.yama.dto

import java.util.*

const val USER_ID = "user_id"
const val CONTENT = "content"
const val CREATED_AT = "created_at"

open class MessageDto(val user: String = "",
                  val content : String = "",
                  val createdAt: Date = Date())/*:Dto<MessageMD>{

    override fun fromObj(obj: MessageMD): Dto<MessageMD> =
            MessageDto(obj.user.login, obj.content, obj.createdAt)

    override fun toObj(dto: Dto<MessageMD>): MessageMD =
            MessageMD()


}

*/
