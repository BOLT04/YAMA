package isel.pt.yama.repository.dataAccess.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class OrganizationDto (val login: String,
                       val id: Int) : Parcelable