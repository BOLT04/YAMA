package isel.pt.yama.model.dataAccess.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.*

@Entity(tableName = "teams", primaryKeys = ["orgId", "id"])
data class Team(
        val name : String,
        val id : Int,
        val orgId : Int,
        val description : String?)

@Entity(tableName = "users")
data class User (
        @PrimaryKey
        val id : Int,
        val login : String,
        val orgId : Int,
        val bio : String,
        val avatarUrl : String,
        val name : String,
        val email : String,
        val followers : Int,
        val following : Int)

@Entity(tableName = "messages", primaryKeys = ["orgId", "teamId", "id"])
data class Message (

        @ForeignKey(entity = Team::class, parentColumns = ["orgId", "id"],
                childColumns = ["orgId", "teamId"])
        val orgId : Int,
        val teamId : Int,

        @PrimaryKey(autoGenerate = true) val id : Int,

        val content : String,
        val createdAt : Timestamp
)


@Entity(tableName = "members", primaryKeys = ["orgId", "teamId", "userId"])
data class Member (

        @ForeignKey(entity = Team::class, parentColumns = ["orgId", "id"],
                    childColumns = ["orgId", "teamId"])
        val orgId : Int,
        val teamId : Int,

        @ForeignKey(entity = User::class, parentColumns = ["id"],
                    childColumns = ["userId"])
        val userId : Int)