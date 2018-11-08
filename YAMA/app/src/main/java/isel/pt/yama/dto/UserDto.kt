package isel.pt.yama.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserDto(
        val login : String,
        val id : Int,
        val avatar_url : String,
        val name : String?,
        val email : String?,
        val bio : String?,
        val followers : Int,
        val following : Int,
        val public_repos : String
) : Parcelable
