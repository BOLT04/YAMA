package isel.pt.yama.model.dataAccess.database

import androidx.room.Entity
import java.util.*

@Entity(tableName = "teams", primaryKeys = arrayOf("orgId", "id"))
data class Team(
        val name : String,
        val id : Int,
        val orgId : Int,
        val description : String?)

@Entity(tableName = "members", primaryKeys = arrayOf("name", "id"))
data class Member (
        val login : String,
        val id : Int,
        val avatar_url : String,
        val name : String,
        val email : String,
        val followers : Int,
        val following : Int)

@Entity(tableName = "messages")
data class Message (
        val content : String,
        val id : Int,
        val date : Date)