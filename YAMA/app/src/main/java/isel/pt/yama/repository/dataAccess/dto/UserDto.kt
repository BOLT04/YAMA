package isel.pt.yama.repository.dataAccess.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserDto(
        val login : String,
        val id : Int,
        val avatar_url : String,
        val name : String?,
        val email : String?,
        val followers : Int?,
        val following : Int) : Parcelable
