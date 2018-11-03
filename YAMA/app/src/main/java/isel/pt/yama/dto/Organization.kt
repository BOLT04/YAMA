package pt.isel.pdm.yama.model

import android.os.Parcel
import android.os.Parcelable
//import kotlinx.android.parcel.Parcelize

//@Parcelize TODO: how to use??
class Organization (val login: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(login)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Organization> {
        override fun createFromParcel(parcel: Parcel) = Organization(parcel)

        override fun newArray(size: Int): Array<Organization?> = arrayOfNulls(size)
    }
}