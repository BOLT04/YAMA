package isel.pt.yama.repository.dataAccess.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "teams", primaryKeys = ["organizationID", "id"])
data class TeamDB (
        val name : String,
        val id : Int,
        @ForeignKey(entity = OrganizationDB::class, parentColumns = ["login"],
                childColumns = ["organizationID"])
        val organizationID : String,
        val description : String?) : Parcelable {

}

@Entity(tableName = "users")
data class UserDB (
        @PrimaryKey
        val login : String,
        val id : Int,
        val name : String?,
        val email : String?,
        val avatarUrl : String,
        val followers : Int?,
        val following : Int)


@Entity(tableName = "team_members", primaryKeys = ["organizationID", "team", "user"])
data class TeamMemberDB (
        @ForeignKey(entity = TeamDB::class, parentColumns = ["organizationID", "id"],
                    childColumns = ["organizationID", "team"])
        val organizationID : String,
        val team : Int,
        @ForeignKey(entity = UserDB::class, parentColumns = ["login"],
                    childColumns = ["user"])
        val user : String)

@Entity(tableName = "organization_members", primaryKeys = ["organizationID", "user"])
data class OrganizationMemberDB (
        @ForeignKey(entity = OrganizationDB::class, parentColumns = ["login"],
                childColumns = ["organizationID"])
        val organizationID : String,

        @ForeignKey(entity = UserDB::class, parentColumns = ["login"],
                childColumns = ["user"])
        val user : String)


@Entity(tableName = "organizations")
data class OrganizationDB (
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
