package pt.isel.pdm.yama.model

import android.os.Parcel
import android.os.Parcelable
//import kotlinx.android.parcel.Parcelize

//@Parcelize
class Team (val name : String,
            val id : Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(id)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Team> {
        override fun createFromParcel(parcel: Parcel) = Team(parcel)

        override fun newArray(size: Int): Array<Team?> = arrayOfNulls(size)
    }
}