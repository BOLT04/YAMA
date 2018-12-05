package isel.pt.yama.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TeamDto (val name : String = "",
               val id : Int = 0,
               val description : String? = null) : Parcelable