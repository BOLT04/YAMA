package isel.pt.yama.dto

import android.os.Parcel
import android.os.Parcelable

class UserDto(
        val login : String,
        val id : Int,
        val avatar_url : String,
        val name : String,
        val email : String,
        val bio : String,
        val followers : Int,
        val following : Int,
        val public_repos : String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(login)
        parcel.writeInt(id)
        parcel.writeString(avatar_url)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(bio)
        parcel.writeInt(followers)
        parcel.writeInt(following)
        parcel.writeString(public_repos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDto> {
        override fun createFromParcel(parcel: Parcel): UserDto {
            return UserDto(parcel)
        }

        override fun newArray(size: Int): Array<UserDto?> {
            return arrayOfNulls(size)
        }
    }
}
