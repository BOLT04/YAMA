package isel.pt.yama.dataAccess.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "teams", primaryKeys = ["organization", "id"])
data class Team (
        val name : String,
        val id : Int,
        @ForeignKey(entity = Organization::class, parentColumns = ["login"],
                childColumns = ["organization"])
        val organization : String,
        val description : String?) : Parcelable

@Entity(tableName = "users")
data class User (
        @PrimaryKey
        val login : String,
        val id : Int,
        val name : String?,
        val email : String?,
        val avatarUrl : String,
        val followers : Int?,
        val following : Int)

@Entity(tableName = "messages")
data class Message (
        @PrimaryKey(autoGenerate = true)
        val id : Int,
        @ForeignKey(entity = Team::class, parentColumns = ["organization", "id"],
                childColumns = ["organization", "team"])
        val organization : String,
        val team : Int,
        val content : String,
        val createdAt : Date
)


@Entity(tableName = "team_members", primaryKeys = ["organization", "team", "user"])
data class TeamMember (
        @ForeignKey(entity = Team::class, parentColumns = ["organization", "id"],
                    childColumns = ["organization", "team"])
        val organization : String,
        val team : Int,
        @ForeignKey(entity = User::class, parentColumns = ["login"],
                    childColumns = ["user"])
        val user : String)

@Entity(tableName = "organization_members", primaryKeys = ["organization", "user"])
data class OrganizationMember (
        @ForeignKey(entity = Organization::class, parentColumns = ["login"],
                childColumns = ["organization"])
        val organization : String,

        @ForeignKey(entity = User::class, parentColumns = ["login"],
                childColumns = ["user"])
        val user : String)

/*data class UserOrganizations (
        @Embedded
        val user : User,
        @Relation(parentColumn = "login", entityColumn = "login", entity = Organization::class)
        val organizations : List<Organization> = listOf()
)

data class TeamMembers (
        @Embedded
        val team : Team,
        @Relation(parentColumn = "login", entityColumn = "login", entity = User::class)
        val users : List<User> = listOf()
)*/

@Entity(tableName = "organizations")
data class Organization (
        @PrimaryKey
        val login : String,
        val id : Int)


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}