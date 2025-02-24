import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.halidao.data.model.GioHangItem
import com.example.halidao.data.model.MenuItem
import com.example.halidao.data.model.Staff
import com.example.halidao.data.model.Statistics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "halidao_database.db" // Tên database
        private const val DATABASE_VERSION = 37// Tăng version để cập nhật da tabase
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Bảng Trạng Thái (Chuẩn hóa trạng thái)
        db.execSQL("""
            CREATE TABLE TrangThai (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ten TEXT NOT NULL UNIQUE
            );
        """)

        // Bảng Khách Hàng
        db.execSQL("""
            CREATE TABLE KhachHang (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ten TEXT NOT NULL,
                sdt TEXT UNIQUE,
                email TEXT UNIQUE,
                diem_tich_luy INTEGER DEFAULT 0
            );
        """)

        // Tạo INDEX cho tìm kiếm nhanh hơn
        db.execSQL("CREATE INDEX idx_khachhang_sdt ON KhachHang(sdt);")
        db.execSQL("CREATE INDEX idx_khachhang_email ON KhachHang(email);")

        // Bảng Bàn Ăn
        db.execSQL("""
            CREATE TABLE BanAn (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                so_ban INTEGER NOT NULL UNIQUE,
                id_trang_thai INTEGER DEFAULT 1,    
                FOREIGN KEY (id_trang_thai) REFERENCES TrangThai(id)
            );
        """)

        // Bảng Danh Mục
        db.execSQL("""
            CREATE TABLE DanhMuc (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                danh_muc TEXT NOT NULL
            );
        """)

        // Bảng Món Ăn
        db.execSQL("""
            CREATE TABLE MonAn (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_danh_muc INTEGER NOT NULL,
                ten_mon TEXT NOT NULL,
                so_tien INTEGER NOT NULL,
                hinh_anh TEXT NOT NULL,
                FOREIGN KEY (id_danh_muc) REFERENCES DanhMuc(id) ON DELETE CASCADE
            );
        """)

        // Bảng Đơn Hàng
        db.execSQL("""
            CREATE TABLE DonHang (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_ban INTEGER NOT NULL,
                id_khach_hang INTEGER,
                ngay INTEGER NOT NULL,
                tong_tien INTEGER NOT NULL,
                id_trang_thai INTEGER DEFAULT 4,
                da_thanh_toan INTEGER DEFAULT 0,
                phuong_thuc_thanh_toan TEXT DEFAULT NULL,
                FOREIGN KEY (id_ban) REFERENCES BanAn(id) ON DELETE CASCADE,
                FOREIGN KEY (id_khach_hang) REFERENCES KhachHang(id) ON DELETE SET NULL,
                FOREIGN KEY (id_trang_thai) REFERENCES TrangThai(id)
            );
        """)

        // INDEX cho nhanh hơn
        db.execSQL("CREATE INDEX idx_donhang_khachhang ON DonHang(id_khach_hang);")

        // Bảng Chi Tiết Đơn Hàng
        db.execSQL("""
            CREATE TABLE ChiTietDonHang (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_don_hang INTEGER NOT NULL,
                id_mon_an INTEGER NOT NULL,
                so_luong INTEGER NOT NULL DEFAULT 1,
                gia INTEGER NOT NULL,
                id_trang_thai INTEGER DEFAULT 4,
                FOREIGN KEY (id_don_hang) REFERENCES DonHang(id) ON DELETE CASCADE,
                FOREIGN KEY (id_mon_an) REFERENCES MonAn(id) ON DELETE CASCADE,
                FOREIGN KEY (id_trang_thai) REFERENCES TrangThai(id)
            );
        """)
        db.execSQL("""
            CREATE TABLE Role (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ten_role TEXT NOT NULL UNIQUE
            );
        """)

        // Bảng Nhân Viên
        db.execSQL("""
        CREATE TABLE NhanVien (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            ten TEXT NOT NULL,
            sdt TEXT UNIQUE,
            email TEXT UNIQUE,
            mat_khau TEXT NOT NULL,
            id_role INTEGER NOT NULL,
            FOREIGN KEY (id_role) REFERENCES Role(id) ON DELETE SET NULL
        );
    """)
        db.execSQL("""
    CREATE TABLE GioHang (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        id_mon_an INTEGER NOT NULL,
        ten_mon TEXT NOT NULL,
        so_tien INTEGER NOT NULL,
        so_luong INTEGER NOT NULL DEFAULT 1,
        FOREIGN KEY (id_mon_an) REFERENCES MonAn(id) ON DELETE CASCADE
    );
""")
        // Thêm tài khoản quản trị viên mặc định
        db.execSQL("""
            INSERT INTO Role (ten_role) VALUES ('Quản lý'), ('Nhân viên');
        """)

        db.execSQL("""
            INSERT INTO NhanVien (ten, sdt, email, mat_khau, id_role) 
            VALUES ('Admin', '0123456789', '23810013@student.hcmute.edu.vn', '123456', 
                (SELECT id FROM Role WHERE ten_role = 'Quản lý' LIMIT 1));
        """)
        db.execSQL("""
            INSERT INTO NhanVien (ten, sdt, email, mat_khau, id_role)
            VALUES ('User1', '0987654321', 'user1@example.com', '123456', 
            (SELECT id FROM Role WHERE ten_role = 'Nhân viên' LIMIT 1));
        """)
        // Bảng Thanh Toán
        db.execSQL("""
            CREATE TABLE ThanhToan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_don_hang INTEGER NOT NULL,
                ngay INTEGER NOT NULL,
                so_tien INTEGER NOT NULL,
                phuong_thuc TEXT NOT NULL,
                FOREIGN KEY (id_don_hang) REFERENCES DonHang(id) ON DELETE CASCADE
            );
        """)

        // Thêm dữ liệu mặc định cho bảng trạng thái
        db.execSQL("INSERT INTO TrangThai (ten) VALUES \n" +
                "('Bàn trống'), \n" +
                "('Bàn đang sử dụng'), \n" +
                "('Bàn đang dọn dẹp'), \n" +
                "('Món chưa làm'), \n" +
                "('Món đang làm'), \n" +
                "('Món đã xong'),\n" +
                "('Đã Thanh Toán'); \n")
// Thêm dữ liệu mẫu vào Bảng Khách Hàng
        db.execSQL("""
        INSERT INTO KhachHang (ten, sdt, email, diem_tich_luy) VALUES 
        ('Nguyễn Văn A', '0987654321', 'nguyenvana@example.com', 100),
        ('Trần Thị B', '0912345678', 'tranthib@example.com', 50),
        ('Lê Văn C', '0934567890', 'levanc@example.com', 20);
    """)

        // Thêm dữ liệu mẫu vào Bảng Bàn Ăn
        db.execSQL("""
        INSERT INTO BanAn (so_ban, id_trang_thai) VALUES 
        (1, 2), (2, 2), (3, 2), (4, 1), (5, 1);
    """)

        // Thêm dữ liệu mẫu vào Bảng Danh Mục Món Ăn
        db.execSQL("""
        INSERT INTO DanhMuc (danh_muc) VALUES 
        ('Món chính'), ('Món phụ'), ('Đồ uống'), ('Tráng miệng');
    """)

        // Thêm dữ liệu mẫu vào Bảng Món Ăn
        db.execSQL("""
        INSERT INTO MonAn (id_danh_muc, ten_mon, so_tien, hinh_anh) VALUES 
        (1, 'Mì xào', 50000, 'mi_xao'),
        (1, 'Phở bò', 60000, 'pho_bo'),
        (1, 'Bún thịt nướng', 55000, 'bun_thit_nuong'),
        (1, 'Bún bò Huế', 60000, 'bun_bo_hue'),
        (1, 'Cơm chiên hải sản', 65000, 'com_chien_hai_san'),
        (1, 'Cơm tấm', 50000, 'com_tam'),
        (2, 'Gỏi cuốn', 40000, 'goi_cuon'),
        (2, 'Bò bía', 30000, 'bo_bia'),
        (3, 'Cà phê sữa', 20000, 'ca_phe_sua'),
        (3, 'Coca cola', 15000, 'coca'),
        (3, '7up', 15000, 'sevent_up'),
        (3, 'Bia Tiger', 15000, 'tiger'),
        (3, 'Bia Heineken', 15000, 'heineken'),
        (3, 'Nước cam', 25000, 'nuoc_cam'),
        (3, 'Aquafina', 10000, 'aquafina'),
        (3, 'Trà sữa', 25000, 'tra_sua'),
        (4, 'Sửa chua', 10000, 'sua_chua'),
        (4, 'kem ốc quê', 10000, 'oc_que'),
        (4, 'Kem ly', 15000, 'kem_ly');
    """)

        // Thêm dữ liệu mẫu vào Bảng Đơn Hàng
        db.execSQL("""
        INSERT INTO DonHang (id_ban, id_khach_hang, ngay, tong_tien, id_trang_thai, da_thanh_toan, phuong_thuc_thanh_toan) VALUES 
        (1, 4, strftime('%s','now'), 50000, 2, 0, NULL),
        (2, 5, strftime('%s','now'), 60000, 2, 0, NULL),
        (3, NULL, strftime('%s','now'), 70000, 2, 0, NULL), (1,NULL, strftime('%s', '2025-01-15 12:00:00'), 70000, 2, 1, 'Tiền mặt'), -- Tháng 1
        (2,NULL ,strftime('%s', '2025-03-10 15:30:00'), 7204, 2, 1, 'Tiền mặt');
    """)

        // Thêm dữ liệu mẫu vào Bảng Chi Tiết Đơn Hàng
        db.execSQL("""
        INSERT INTO ChiTietDonHang (id_don_hang, id_mon_an, so_luong, gia, id_trang_thai) VALUES 
        (1, 1, 2, 50000, 4),
        (2, 2, 1, 60000, 5),
        (3, 3, 3, 40000, 3);
    """)

        // Thêm dữ liệu mẫu vào Bảng Thanh Toán
        db.execSQL("""
        INSERT INTO ThanhToan (id_don_hang, ngay, so_tien, phuong_thuc) VALUES 
        (3, strftime('%s','now'), 70000, 'Tiền mặt');
    """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS BanAn")
        db.execSQL("DROP TABLE IF EXISTS KhachHang")
        db.execSQL("DROP TABLE IF EXISTS TrangThai")
        db.execSQL("DROP TABLE IF EXISTS DanhMuc")
        db.execSQL("DROP TABLE IF EXISTS MonAn")
        db.execSQL("DROP TABLE IF EXISTS DonHang")
        db.execSQL("DROP TABLE IF EXISTS ChiTietDonHang")
        db.execSQL("DROP TABLE IF EXISTS ThanhToan")
        db.execSQL("DROP TABLE IF EXISTS Role")
        db.execSQL("DROP TABLE IF EXISTS NhanVien")
        db.execSQL("DROP TABLE IF EXISTS GioHang")
        onCreate(db) // Gọi lại để tạo bảng mới
    }
    // Thêm món vào giỏ hàng
    fun addToCart(monAn: MenuItem, soLuong: Int) {
        val db = writableDatabase
        try {
            db.rawQuery("SELECT so_luong FROM GioHang WHERE id_mon_an = ?", arrayOf(monAn.id.toString())).use { cursor ->
                val values = ContentValues().apply {
                    put("id_mon_an", monAn.id)
                    put("ten_mon", monAn.tenMon)
                    put("so_tien", monAn.gia)
                }

                if (cursor.moveToFirst()) {
                    // Nếu món ăn đã có trong giỏ, cộng thêm số lượng mới
                    val newQuantity = cursor.getInt(0) + soLuong
                    values.put("so_luong", newQuantity)
                    db.update("GioHang", values, "id_mon_an = ?", arrayOf(monAn.id.toString()))
                } else {
                    // Nếu chưa có, thêm mới với số lượng được chỉ định
                    values.put("so_luong", soLuong)
                    db.insert("GioHang", null, values)
                }
            }
        } finally {
            db.close()
        }
    }

    // Lấy danh sách món trong giỏ hàng
    fun getCartItems(): List<GioHangItem> {
        val cartItems = mutableListOf<GioHangItem>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM GioHang", null)

        while (cursor.moveToNext()) {
            val gioHangItem = GioHangItem(
                id = cursor.getInt(0),       // ID của giỏ hàng (tự động tăng)
                idMonAn = cursor.getInt(1),  // ID của món ăn
                tenMon = cursor.getString(2), // Tên món ăn
                soTien = cursor.getInt(3),   // Giá tiền
                soLuong = cursor.getInt(4)   // Số lượng món ăn
            )
            cartItems.add(gioHangItem)
        }

        cursor.close()
        db.close()
        return cartItems
    }

    fun updateCartItem(idMonAn: Int, soLuong: Int) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put("so_luong", soLuong)
            }
            db.update("GioHang", values, "id_mon_an = ?", arrayOf(idMonAn.toString()))
        } finally {
            db.close()
        }
    }

    fun getCartItemById(idMonAn: Int): GioHangItem? {
        val db = readableDatabase
        var cartItem: GioHangItem? = null
        try {
            db.rawQuery("SELECT id_mon_an, ten_mon, so_luong, so_tien FROM GioHang WHERE id_mon_an = ?",
                arrayOf(idMonAn.toString())
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getInt(0)
                    val tenMon = cursor.getString(1)
                    val soLuong = cursor.getInt(2)
                    val soTien = cursor.getInt(3)
                    cartItem = GioHangItem(idMonAn, idMonAn, tenMon, soTien, soLuong)
                }
            }
        } finally {
            db.close()
        }
        return cartItem
    }

    // Xóa món khỏi giỏ hàng
    fun removeFromCart(idMonAn: Int) {
        val db = writableDatabase
        db.delete("GioHang", "id_mon_an=?", arrayOf(idMonAn.toString()))
        db.close()
    }

    // Xóa toàn bộ giỏ hàng khi xác nhận đơn
    fun clearCart() {
        val db = writableDatabase
        db.delete("GioHang", null, null)
        db.close()
    }

    fun getAllOrders(): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = """
        SELECT DonHang.id, BanAn.so_ban, DonHang.tong_tien, TrangThai.ten, 
               DonHang.da_thanh_toan, DonHang.id_khach_hang, DonHang.ngay 
        FROM DonHang
        JOIN BanAn ON DonHang.id_ban = BanAn.id
        JOIN TrangThai ON DonHang.id_trang_thai = TrangThai.id
    """
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val order = Order(
                id = cursor.getInt(0),
                idBan = cursor.getInt(1),
                tongTien = if (cursor.isNull(2)) 0 else cursor.getInt(2),
                trangThai = cursor.getInt(3),
                daThanhToan = cursor.getInt(4) == 1,
                idKhachHang = if (cursor.isNull(5)) null else cursor.getInt(5),
                ngay = cursor.getString(6) ?: "Không có dữ liệu"
            )
            orders.add(order)
        }
        cursor.close()
        return orders
    }


    fun getOrdersByStatus(statusId: Int): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = """
        SELECT DonHang.id, DonHang.id_ban, BanAn.so_ban, DonHang.tong_tien, DonHang.id_trang_thai, 
               DonHang.da_thanh_toan, DonHang.id_khach_hang, DonHang.ngay 
        FROM DonHang
        JOIN BanAn ON DonHang.id_ban = BanAn.id  -- ✅ Đảm bảo lấy thông tin bàn
        WHERE DonHang.id_trang_thai = ?
    """
        val cursor = db.rawQuery(query, arrayOf(statusId.toString()))

        while (cursor.moveToNext()) {
            val order = Order(
                id = cursor.getInt(0),
                idBan = cursor.getInt(1), // ✅ Đảm bảo lấy id_ban
                tongTien = cursor.getInt(3),
                trangThai = cursor.getInt(4),
                daThanhToan = cursor.getInt(5) == 1,
                idKhachHang = if (cursor.isNull(6)) null else cursor.getInt(6),
                ngay = cursor.getString(7)
            )
            orders.add(order)
        }
        cursor.close()
        return orders
    }

    fun updateOrderStatus(orderId: Int, currentStatus: Int, newStatus: Int, orderDetailId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_trang_thai", newStatus)
        }

        val rowsUpdated = db.update(
            "ChiTietDonHang",
            values,
            "id_don_hang = ? AND id_trang_thai = ? AND id = ?",
            arrayOf(orderId.toString(), currentStatus.toString(), orderDetailId.toString())
        )

        Log.d("DatabaseHelper", "Update order detail: orderId=$orderId, orderDetailId=$orderDetailId, from $currentStatus to $newStatus, rows updated: $rowsUpdated")

        db.close()
        return rowsUpdated > 0
    }


    fun payOrder(orderId: Int, amount: Int, paymentMethod: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("da_thanh_toan", 1)
                put("phuong_thuc_thanh_toan", paymentMethod)  // Lưu phương thức thanh toán
                put("id_trang_thai", 7)  // Trạng thái "Đã thanh toán"
            }
            db.update("DonHang", values, "id = ?", arrayOf(orderId.toString()))

            val paymentValues = ContentValues().apply {
                put("id_don_hang", orderId)
                put("ngay", System.currentTimeMillis())
                put("so_tien", amount)
                put("phuong_thuc", paymentMethod)
            }
            db.insert("ThanhToan", null, paymentValues)

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            Log.e("PayOrder", "Lỗi thanh toán: ${e.message}")
            return false
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    fun markOrderAsPaid(orderId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("da_thanh_toan", 1)
        }
        val rowsUpdated = db.update("DonHang", values, "id = ?", arrayOf(orderId.toString()))
        db.close()
        return rowsUpdated > 0
    }

    fun insertOrder(idBan: Int, ngay: Long?, tongTien: Int, trangThai: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_ban", idBan)
            put("ngay", ngay ?: System.currentTimeMillis() / 1000) // ✅ Luôn lưu timestamp (INTEGER)
            put("tong_tien", tongTien)
            put("id_trang_thai", trangThai)
        }

        return try {
            val result = db.insert("DonHang", null, values)
            if (result == -1L) {
                Log.e("DatabaseHelper", "Thêm đơn hàng thất bại!")
            } else {
                Log.d("DatabaseHelper", "Thêm đơn hàng thành công với ID: $result")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Lỗi khi thêm đơn hàng: ${e.message}")
            -1L
        }
    }

    fun getOrderDetailsByStatus(orderId: Int, status: Int): List<OrderDetail> {
        val items = mutableListOf<OrderDetail>()
        val db = readableDatabase

        val query = """
        SELECT ChiTietDonHang.id, MonAn.ten_mon, ChiTietDonHang.so_luong, ChiTietDonHang.gia, ChiTietDonHang.id_trang_thai 
        FROM ChiTietDonHang 
        JOIN MonAn ON ChiTietDonHang.id_mon_an = MonAn.id
        JOIN DonHang ON ChiTietDonHang.id_don_hang = DonHang.id  
        WHERE ChiTietDonHang.id_don_hang = ? AND DonHang.da_thanh_toan = 0 AND ChiTietDonHang.id_trang_thai = ?
    """

        val cursor = db.rawQuery(query, arrayOf(orderId.toString(), status.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val tenMon = cursor.getString(1)
                val soLuong = cursor.getInt(2)
                val gia = cursor.getInt(3)
                val trangThai = cursor.getInt(4) // ✅ Thêm trạng thái để đảm bảo dữ liệu đúng
                items.add(OrderDetail(id, tenMon, soLuong, gia, trangThai))
            } while (cursor.moveToNext())
        }
        Log.d("DatabaseHelper", "Lấy món từ DB: orderId=$orderId, statusId=$status")

        cursor.close()
        return items
    }



    fun getAllMenuItems(): List<MenuItem> {
        val menuItems = mutableListOf<MenuItem>()
        val db = readableDatabase
        val query = """
        SELECT MonAn.id, MonAn.ten_mon, MonAn.so_tien, MonAn.hinh_anh, MonAn.id_danh_muc 
        FROM MonAn
    """  // ✅ Sửa lỗi: Lấy đúng id_danh_muc
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val menuItem = MenuItem(
                id = cursor.getInt(0),
                tenMon = cursor.getString(1),
                gia = cursor.getInt(2),
                hinhAnh = cursor.getString(3),
                danhMuc = cursor.getInt(4) // ✅ Đã sửa: Giữ nguyên ID danh mục (INT)
            )
            menuItems.add(menuItem)
        }
        cursor.close()
        Log.d("DatabaseHelper", "Lấy danh sách món ăn thành công: $menuItems")
        return menuItems
    }


    fun updateTableStatus(tableId: Int, newStatusId: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            // ✅ Cập nhật trạng thái bàn
            val tableValues = ContentValues().apply {
                put("id_trang_thai", newStatusId)
            }
            db.update("BanAn", tableValues, "id = ?", arrayOf(tableId.toString()))

            // ✅ Nếu bàn chuyển về "Trống", không tạo đơn hàng mới ngay lập tức
            if (newStatusId == 1) { // 1 = Bàn trống
                val orderValues = ContentValues().apply {
                    put("id_trang_thai", 7) // ✅ Đánh dấu đơn hàng là "Đã thanh toán"
                }
                db.update("DonHang", orderValues, "id_ban = ? AND da_thanh_toan = 0", arrayOf(tableId.toString()))
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    fun getOrdersByTableStatus(statusId: Int): List<Order> {
        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val query = """
        SELECT BanAn.id, BanAn.so_ban, COALESCE(DonHang.tong_tien, 0), BanAn.id_trang_thai,
               COALESCE(DonHang.da_thanh_toan, 0), COALESCE(DonHang.id_khach_hang, NULL),
               COALESCE(DonHang.ngay, '')
        FROM BanAn
        LEFT JOIN DonHang ON BanAn.id = DonHang.id_ban AND DonHang.da_thanh_toan = 0
        WHERE BanAn.id_trang_thai = ?
    """
        val cursor = db.rawQuery(query, arrayOf(statusId.toString()))

        while (cursor.moveToNext()) {
            val order = Order(
                id = cursor.getInt(0),
                idBan = cursor.getInt(1),
                tongTien = cursor.getInt(2),
                trangThai = cursor.getInt(3),
                daThanhToan = cursor.getInt(4) == 1,
                idKhachHang = if (cursor.isNull(5)) null else cursor.getInt(5),
                ngay = cursor.getString(6)
            )
            orders.add(order)
        }
        cursor.close()
        return orders
    }

    fun deleteOldOrdersForTable(tableId: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("DonHang", "id_ban = ? AND da_thanh_toan = 1", arrayOf(tableId.toString()))
        db.close()
        return rowsDeleted > 0
    }

    fun deleteFood(foodId: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("MonAn", "id = ?", arrayOf(foodId.toString()))
        db.close()
        return rowsDeleted > 0
    }

    fun updateFood(id: Int, tenMon: String, gia: Int, hinhAnh: String, idDanhMuc: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("ten_mon", tenMon)
            put("so_tien", gia)  // ⛔ LỖI cũ là `gia` nhưng đúng là `so_tien`
            put("hinh_anh", hinhAnh)
            put("id_danh_muc", idDanhMuc)  // ✅ Lưu ID danh mục đúng cách
        }

        val result = db.update("MonAn", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun insertFood(food: MenuItem): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("ten_mon", food.tenMon)
            put("so_tien", food.gia)
            put("id_danh_muc", 1) // Mặc định danh mục ID = 1
            put("hinh_anh", food.hinhAnh)
        }
        return db.insert("MonAn", null, values)
    }
    fun getRevenueStatistics(): List<Statistics> {
        val statisticsList = mutableListOf<Statistics>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT strftime('%Y-%m', " +
                    "   CASE " +
                    "       WHEN typeof(ngay) = 'integer' THEN datetime(ngay, 'unixepoch', 'localtime') " +
                    "       ELSE ngay " +
                    "   END) AS month, " +
                    "   COALESCE(SUM(tong_tien), 0) " +
                    "FROM DonHang " +
                    "WHERE ngay IS NOT NULL " +
                    "GROUP BY month " +
                    "ORDER BY month DESC",
            null
        )

        while (cursor.moveToNext()) {
            val month = cursor.getString(0) ?: "Không có dữ liệu"
            val revenue = cursor.getInt(1)
            statisticsList.add(Statistics(month, revenue))
        }

        cursor.close()
        db.close()
        return statisticsList
    }




    fun getTotalRevenue(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(tong_tien) FROM DonHang", null)
        var totalRevenue = 0
        if (cursor.moveToFirst()) {
            totalRevenue = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return totalRevenue
    }

    fun getTotalOrders(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM DonHang WHERE id_trang_thai != 7", null) // Loại bỏ đơn hàng đã thanh toán
        var totalOrders = 0
        if (cursor.moveToFirst()) {
            totalOrders = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return totalOrders
    }


    fun getRevenueByCategory(): Map<String, Int> {
        val revenueMap = mutableMapOf<String, Int>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT DanhMuc.danh_muc, SUM(DonHang.tong_tien) FROM DonHang " +
                    "JOIN ChiTietDonHang ON DonHang.id = ChiTietDonHang.id_don_hang " +
                    "JOIN MonAn ON ChiTietDonHang.id_mon_an = MonAn.id " +
                    "JOIN DanhMuc ON MonAn.id_danh_muc = DanhMuc.id " +
                    "GROUP BY DanhMuc.danh_muc",
            null
        )

        while (cursor.moveToNext()) {
            val category = cursor.getString(0)
            val revenue = cursor.getInt(1)
            revenueMap[category] = revenue
        }

        cursor.close()
        db.close()
        return revenueMap
    }

    fun getDailyRevenue(): List<Pair<String, Int>> {
        val dailyRevenueList = mutableListOf<Pair<String, Int>>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT strftime('%Y-%m-%d', ngay), SUM(tong_tien) FROM DonHang GROUP BY strftime('%Y-%m-%d', ngay)",
            null
        )

        while (cursor.moveToNext()) {
            val date = cursor.getString(0)
            val revenue = cursor.getInt(1)
            dailyRevenueList.add(Pair(date, revenue))
        }

        cursor.close()
        db.close()
        return dailyRevenueList
    }

    fun getAllStaff(): List<Staff> {
        val staffList = mutableListOf<Staff>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, ten, sdt, email, mat_khau, id_role FROM NhanVien", null)

        while (cursor.moveToNext()) {
            val staff = Staff(
                id = cursor.getInt(0),
                ten = cursor.getString(1),
                sdt = cursor.getString(2),
                email = cursor.getString(3),
                matKhau = cursor.getString(4),
                idRole = cursor.getInt(5)
            )
            staffList.add(staff)
        }
        cursor.close()
        return staffList
    }

    fun insertStaff(staff: Staff): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("ten", staff.ten)
            put("sdt", staff.sdt)
            put("email", staff.email)
            put("mat_khau", staff.matKhau)
            put("id_role", staff.idRole)
        }
        val result = db.insert("NhanVien", null, values)
        db.close()
        return result != -1L
    }

    fun updateStaff(id: Int, name: String, phone: String, email: String, roleId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("ten", name)
            put("sdt", phone)
            put("email", email)
            put("id_role", roleId)
        }
        val result = db.update("NhanVien", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun deleteStaff(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("NhanVien", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun insertDonHang(idBan: Int, ngay: Long, tongTien: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_ban", idBan)
            put("ngay", ngay)
            put("tong_tien", tongTien)
        }
        return db.insert("DonHang", null, values)
    }

    fun insertChiTietDonHang(idDonHang: Int, idMonAn: Int, soLuong: Int, gia: Int) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("id_don_hang", idDonHang)
                put("id_mon_an", idMonAn)
                put("so_luong", soLuong)
                put("gia", gia)
            }
            db.insert("ChiTietDonHang", null, values)

            // Cập nhật tổng tiền của DonHang
            db.execSQL(
                """
            UPDATE DonHang 
            SET tong_tien = (
                SELECT SUM(gia * so_luong) 
                FROM ChiTietDonHang 
                WHERE id_don_hang = ?
            )
            WHERE id = ?
            """, arrayOf(idDonHang, idDonHang)
            )

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
    fun deleteOrderDetails(orderId: Int): Boolean {
        val db = writableDatabase
        val rowsDeleted = db.delete("ChiTietDonHang", "id_don_hang = ?", arrayOf(orderId.toString()))
        db.close()
        return rowsDeleted > 0
    }
    fun updateOrderAsPaid(orderId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("da_thanh_toan", 1)
            put("phuong_thuc_thanh_toan", "Tiền mặt")
        }
        val rowsUpdated = db.update("DonHang", values, "id = ?", arrayOf(orderId.toString()))
        db.close()
        return rowsUpdated > 0
    }
    fun getLatestUnpaidOrder(idBan: Int): Int {
        val db = readableDatabase
        val query = """
        SELECT id FROM DonHang 
        WHERE id_ban = ? AND da_thanh_toan = 0 
        ORDER BY id DESC LIMIT 1
    """
        val cursor = db.rawQuery(query, arrayOf(idBan.toString()))

        var orderId = -1
        if (cursor.moveToFirst()) {
            orderId = cursor.getInt(0)
        }

        cursor.close()
        Log.d("DatabaseHelper", "🔥 Debug getLatestUnpaidOrder: Bàn $idBan, orderId=$orderId")
        return orderId
    }

    fun updateOrderDetailsToNewOrder(oldOrderId: Int, newOrderId: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_don_hang", newOrderId) // Cập nhật đơn hàng mới
        }

        val rowsUpdated = db.update(
            "ChiTietDonHang",
            values,
            "id_don_hang = ?",
            arrayOf(oldOrderId.toString())
        )

        db.close()
        return rowsUpdated
    }



    fun isOrderPaid(orderId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT da_thanh_toan FROM DonHang WHERE id = ?", arrayOf(orderId.toString()))
        var isPaid = false
        if (cursor.moveToFirst()) {
            isPaid = cursor.getInt(0) == 1
        }
        cursor.close()
        db.close()
        return isPaid
    }
    fun areAllItemsCompleted(orderId: Int): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM ChiTietDonHang WHERE id_don_hang = ? AND id_trang_thai != 6"
        val cursor = db.rawQuery(query, arrayOf(orderId.toString()))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return count == 0 // ✅ Nếu không còn món nào chưa hoàn thành, trả về true
    }

    fun getFoodImageById(foodId: Int): String {
        val db = readableDatabase
        var imageName = ""
        val query = "SELECT hinh_anh FROM MonAn WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(foodId.toString()))

        if (cursor.moveToFirst()) {
            imageName = cursor.getString(0) ?: "" // Lấy tên ảnh từ database
        }

        cursor.close()
        return imageName
    }

    fun getCategoryNameById(categoryId: Int): String? {
        val db = readableDatabase
        val query = "SELECT danh_muc FROM DanhMuc WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(categoryId.toString()))

        var categoryName: String? = null
        if (cursor.moveToFirst()) {
            categoryName = cursor.getString(0)  // Lấy tên danh mục
        }
        cursor.close()
        return categoryName
    }
    fun getAllCategories(): List<Pair<Int, String>> {
        val categories = mutableListOf<Pair<Int, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, danh_muc FROM DanhMuc", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            categories.add(Pair(id, name))
        }

        cursor.close()
        db.close()
        return categories
    }
    fun getBestSellingFoods(): List<FoodStats> {
        val db = readableDatabase
        val query = """
        SELECT MonAn.ten_mon, 
               SUM(ChiTietDonHang.so_luong) AS total_quantity, 
               SUM(ChiTietDonHang.so_luong * MonAn.so_tien) AS total_revenue
        FROM ChiTietDonHang
        JOIN MonAn ON ChiTietDonHang.id_mon_an = MonAn.id
        GROUP BY MonAn.ten_mon
        ORDER BY total_quantity DESC
        LIMIT 10;
    """
        val cursor = db.rawQuery(query, null)
        val list = mutableListOf<FoodStats>()

        while (cursor.moveToNext()) {
            list.add(FoodStats(cursor.getString(0), cursor.getInt(1), cursor.getInt(2)))
        }
        cursor.close()
        return list
    }

    fun getMostExpensiveFoods(): List<FoodStats> {
        val db = readableDatabase
        val query = """
        SELECT ten_mon, so_tien
        FROM MonAn
        ORDER BY so_tien DESC
        LIMIT 10;
    """
        val cursor = db.rawQuery(query, null)
        val list = mutableListOf<FoodStats>()

        while (cursor.moveToNext()) {
            list.add(FoodStats(cursor.getString(0), cursor.getInt(1), 0))
        }
        cursor.close()
        return list
    }
    fun getTotalFoodSales(): List<FoodStats> {
        val db = readableDatabase
        val query = """
        SELECT MonAn.ten_mon, SUM(ChiTietDonHang.so_luong) AS total_quantity
        FROM ChiTietDonHang
        JOIN MonAn ON ChiTietDonHang.id_mon_an = MonAn.id
        GROUP BY MonAn.ten_mon
        ORDER BY total_quantity DESC;
    """
        val cursor = db.rawQuery(query, null)
        val list = mutableListOf<FoodStats>()

        while (cursor.moveToNext()) {
            list.add(FoodStats(cursor.getString(0), cursor.getInt(1), 0))
        }
        cursor.close()
        return list
    }
    fun getWeeklyRevenue(): List<Pair<String, Int>> {
        val db = readableDatabase
        val query = """
        SELECT strftime('%w', ngay) AS thu, SUM(tong_tien) AS doanh_thu
        FROM DonHang
        WHERE da_thanh_toan = 1
        GROUP BY thu
        ORDER BY thu;
    """
        val cursor = db.rawQuery(query, null)
        val list = mutableListOf<Pair<String, Int>>()

        while (cursor.moveToNext()) {
            list.add(Pair(cursor.getString(0), cursor.getInt(1)))
        }
        cursor.close()
        return list
    }
    fun getTableUsageStats(): List<Pair<Int, Int>> {
        val db = readableDatabase
        val query = """
        SELECT id_ban, COUNT(*) AS so_lan_su_dung
        FROM DonHang
        WHERE da_thanh_toan = 1
        GROUP BY id_ban
        ORDER BY so_lan_su_dung DESC;
    """
        val cursor = db.rawQuery(query, null)
        val list = mutableListOf<Pair<Int, Int>>()

        while (cursor.moveToNext()) {
            list.add(Pair(cursor.getInt(0), cursor.getInt(1)))
        }
        cursor.close()
        return list
    }
    fun getTotalAmount(orderId: Int): Int {
        val db = readableDatabase
        val query = "SELECT SUM(gia * so_luong) FROM ChiTietDonHang WHERE id_don_hang = ?"
        val cursor = db.rawQuery(query, arrayOf(orderId.toString()))

        var totalAmount = 0
        if (cursor.moveToFirst()) {
            totalAmount = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return totalAmount
    }

}
