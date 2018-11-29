package isel.pt.yama.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class OrganizationDto (val login: String,
                       val id: Int) : Parcelable