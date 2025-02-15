package com.example.halidao.data.model

import android.os.Parcel
import android.os.Parcelable

data class MenuItem(
    val id: Int,        // Thêm ID
    val tenMon: String,
    val gia: Int,
    val hinhAnh: String,
    val danhMuc: String // Thêm danh mục
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),    // Đọc ID
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "" // Đọc danh mục
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)  // Ghi ID
        parcel.writeString(tenMon)
        parcel.writeInt(gia)
        parcel.writeString(hinhAnh)
        parcel.writeString(danhMuc) // Ghi danh mục
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MenuItem> {
        override fun createFromParcel(parcel: Parcel): MenuItem {
            return MenuItem(parcel)
        }

        override fun newArray(size: Int): Array<MenuItem?> {
            return arrayOfNulls(size)
        }
    }
}
