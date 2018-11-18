package isel.pt.yama.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Team (val name : String,
            val id : Int,
            val description : String?) : Parcelable