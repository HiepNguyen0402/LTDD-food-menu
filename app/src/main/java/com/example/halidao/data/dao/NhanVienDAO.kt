import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class NhanVienDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun themNhanVien(ten: String, sdt: String, email: String, chucVu: String, matKhau: String): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("ten", ten)
            put("sdt", sdt)
            put("email", email)
            put("chuc_vu", chucVu)
            put("mat_khau", matKhau)  // Lưu plaintext, nhưng nên mã hóa!
        }
        return db.insert("NhanVien", null, values)
    }

    fun kiemTraDangNhap(email: String, matKhau: String): Boolean {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT id FROM NhanVien WHERE email = ? AND mat_khau = ?"
        val cursor = db.rawQuery(query, arrayOf(email, matKhau))
        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }
}
