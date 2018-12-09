package isel.pt.yama.repository.dataAccess.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TeamDto (val name : String = "",
               val id : Int = 0,
               val description : String? = null) : Parcelable