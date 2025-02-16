package com.example.halidao.data.model

data class GioHangItem(
    val id: Int = 0,  // SQLite tự động tăng ID
    val idMonAn: Int,
    val tenMon: String,
    val soTien: Int,
    val soLuong: Int
)