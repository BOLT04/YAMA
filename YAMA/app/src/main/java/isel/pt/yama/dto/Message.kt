package isel.pt.yama.dto

import android.os.Parcel
import android.os.Parcelable

class Message(val user: UserDto,
              val text : String,
              val createdAt: Long):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(UserDto::class.java.classLoader)!!,
            parcel.readString()!!,
            parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(user, flags)
        parcel.writeString(text)
        parcel.writeLong(createdAt)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel) = Message(parcel)

        override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)

    }
}