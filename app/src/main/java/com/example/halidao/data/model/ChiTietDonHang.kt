package com.example.halidao.data.model

data class ChiTietDonHang(
    val id: Int = 0,  // SQLite tự động tăng ID
    val idDonHang: Int,
    val idMonAn: Int,
    val soLuong: Int,
    val gia: Int,
    val idTrangThai: Int = 4  // 4 = Đang làm
)