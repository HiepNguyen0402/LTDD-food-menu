import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.halidao.data.model.MenuItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "halidao_database.db" // Tên database
        private const val DATABASE_VERSION = 15// Tăng version để cập nhật database
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

        // Thêm tài khoản quản trị viên mặc định
        db.execSQL("""
            INSERT INTO Role (ten_role) VALUES ('Quản lý'), ('Nhân viên');
        """)

        db.execSQL("""
            INSERT INTO NhanVien (ten, sdt, email, mat_khau, id_role) 
            VALUES ('Admin', '0123456789', 'admin@example.com', '123456', 
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
        ('Món chính'), ('Món phụ'), ('Đồ uống');
    """)

        // Thêm dữ liệu mẫu vào Bảng Món Ăn
        db.execSQL("""
        INSERT INTO MonAn (id_danh_muc, ten_mon, so_tien, hinh_anh) VALUES 
        (1, 'Mì xào', 50000, 'mi_xao.jpg'),
        (1, 'Phở bò', 60000, 'pho_bo.jpg'),
        (2, 'Gỏi cuốn', 40000, 'goi_cuon.jpg'),
        (3, 'Cà phê sữa', 25000, 'ca_phe_sua.jpg');
    """)

        // Thêm dữ liệu mẫu vào Bảng Đơn Hàng
        db.execSQL("""
        INSERT INTO DonHang (id_ban, id_khach_hang, ngay, tong_tien, id_trang_thai, da_thanh_toan, phuong_thuc_thanh_toan) VALUES 
        (1, 4, strftime('%s','now'), 50000, 2, 0, NULL),
        (2, 5, strftime('%s','now'), 60000, 2, 0, NULL),
        (3, NULL, strftime('%s','now'), 70000, 2, 0, 'Tiền mặt');
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
        onCreate(db) // Gọi lại để tạo bảng mới
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
                tongTien = cursor.getInt(2),
                trangThai = cursor.getInt(3),
                daThanhToan = cursor.getInt(4) == 1,
                idKhachHang = if (cursor.isNull(5)) null else cursor.getInt(5),
                ngay = cursor.getString(6)
            )
            orders.add(order)
        }
        cursor.close()
        Log.d("DatabaseHelper", "SQL Query: " + query);

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



    fun updateOrderStatus(orderId: Int, currentStatus: Int, newStatus: Int): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("id_trang_thai", newStatus) // ✅ Cập nhật trạng thái món ăn trong `ChiTietDonHang`
        }

        val rowsUpdated = db.update(
            "ChiTietDonHang",
            values,
            "id_don_hang = ? AND id_trang_thai = ?",
            arrayOf(orderId.toString(), currentStatus.toString())
        )

        db.close()
        return rowsUpdated > 0
    }


    fun payOrder(orderId: Int, amount: Int, paymentMethod: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Cập nhật đơn hàng thành đã thanh toán
            val values = ContentValues()
            values.put("da_thanh_toan", 1)
            values.put("phuong_thuc_thanh_toan", paymentMethod)
            db.update("DonHang", values, "id = ?", arrayOf(orderId.toString()))

            // Ghi nhận vào bảng ThanhToan
            val paymentValues = ContentValues()
            paymentValues.put("id_don_hang", orderId)
            paymentValues.put("ngay", System.currentTimeMillis())
            paymentValues.put("so_tien", amount)
            paymentValues.put("phuong_thuc", paymentMethod)
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
    fun insertOrder(idBan: Int, idKhachHang: Int?, tongTien: Int, trangThai: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id_ban", idBan)
            put("id_khach_hang", idKhachHang) // Thêm id khách hàng (nếu có)
            put("tong_tien", tongTien)
            put("id_trang_thai", trangThai)
            put("ngay", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())) // Định dạng ngày
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
        } finally {
            db.close()
        }
    }
    fun getOrderDetailsByStatus(orderId: Int, status: Int): List<OrderDetail> {
        val items = mutableListOf<OrderDetail>()
        val db = readableDatabase
        val query = """
        SELECT MonAn.ten_mon, ChiTietDonHang.so_luong, ChiTietDonHang.gia 
        FROM ChiTietDonHang 
        JOIN MonAn ON ChiTietDonHang.id_mon_an = MonAn.id
        WHERE ChiTietDonHang.id_don_hang = ? AND ChiTietDonHang.id_trang_thai = ?
    """ // ✅ Thêm điều kiện lọc theo trạng thái món ăn

        val cursor = db.rawQuery(query, arrayOf(orderId.toString(), status.toString()))

        if (cursor.moveToFirst()) {
            do {
                val tenMon = cursor.getString(0) // Lấy tên món ăn
                val soLuong = cursor.getInt(1)   // Lấy số lượng
                val gia = cursor.getInt(2)       // Lấy giá
                items.add(OrderDetail(tenMon, soLuong, gia))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun getAllMenuItems(): List<MenuItem> {
        val menuItems = mutableListOf<MenuItem>()
        val db = readableDatabase
        val query = """
        SELECT MonAn.id, MonAn.ten_mon, MonAn.so_tien, MonAn.hinh_anh, DanhMuc.danh_muc 
        FROM MonAn
        JOIN DanhMuc ON MonAn.id_danh_muc = DanhMuc.id
    """
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val menuItem = MenuItem(
                id = cursor.getInt(0),
                tenMon = cursor.getString(1),
                gia = cursor.getInt(2),
                hinhAnh = cursor.getString(3),
                danhMuc = cursor.getString(4)
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

            // ✅ Nếu bàn chuyển về "Trống", cập nhật trạng thái hóa đơn thành 7 (Đã thanh toán)
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



}
