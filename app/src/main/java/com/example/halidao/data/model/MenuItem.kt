package com.example.halidao.data.model

import android.os.Parcel
import android.os.Parcelable

data class MenuItem(
    val id: Int,        // Thêm ID
    val tenMon: String,
    val gia: Int,
    val hinhAnh: String,
    val danhMuc: Int // Thêm danh mục
)