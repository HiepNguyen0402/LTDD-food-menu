data class Order(
    val id: Int,
    val idBan: Int,
    val idKhachHang: Int?,
    val ngay: String,
    val tongTien: Int,
    val trangThai: Int,  // 1: Chưa làm, 2: Đang làm, 3: Đã ra món
    val daThanhToan: Boolean
)
