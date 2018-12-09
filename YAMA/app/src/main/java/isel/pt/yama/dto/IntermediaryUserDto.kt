package isel.pt.yama.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class IntermediaryUserDto(
        val login: String,
        val id: Long,
        val avatar_url: String
) : Parcelable