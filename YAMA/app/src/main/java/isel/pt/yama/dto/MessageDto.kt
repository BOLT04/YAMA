package isel.pt.yama.dto

import java.util.*

const val USER_ID = "user_id"
const val CONTENT = "content"
const val CREATED_AT = "created_at"

open class MessageDto(val user: String = "",
                  val content : String = "",
                  val createdAt: Date = Date())
