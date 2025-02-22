data class OrderDetail(
    val orderDetailId: Int,
    val tenMon: String,
    val soLuong: Int,
    val gia: Int,
    val trangThai: Int  // ✅ Thêm trạng thái vào đây
)
