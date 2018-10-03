package com.anywherecat.app.model

import android.os.Parcel
import android.os.Parcelable

 data class CollectModel(val id:Int =0, val note:String="",val address:String="",val latitude:Double=0.0, val longitude:Double=0.0): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(note)
        parcel.writeString(address)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CollectModel> {
        override fun createFromParcel(parcel: Parcel): CollectModel {
            return CollectModel(parcel)
        }

        override fun newArray(size: Int): Array<CollectModel?> {
            return arrayOfNulls(size)
        }
    }

}