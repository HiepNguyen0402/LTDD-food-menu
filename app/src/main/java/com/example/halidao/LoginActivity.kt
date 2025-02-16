package com.example.halidao

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Khởi tạo dbHelper
        dbHelper = DatabaseHelper(this)

        edtUsername = findViewById(R.id.et_Email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            val user = checkLogin(username, password) // Lấy thông tin user từ DB

            if (user != null) { // Nếu đăng nhập thành công
                val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("name", user.first) // Lưu tên nhân viên
                    putString("role", user.second)    // Lưu tên role
                    putString("email", user.third)
                    apply() // Lưu thay đổi
                }
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun checkLogin(username: String, password: String): Triple<String, String, String>? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT NhanVien.ten, Role.ten_role, NhanVien.email
        FROM NhanVien 
        INNER JOIN Role ON NhanVien.id_role = Role.id
        WHERE (NhanVien.sdt = ? OR NhanVien.email = ?) 
        AND NhanVien.mat_khau = ?
    """
        val cursor = db.rawQuery(query, arrayOf(username, username, password))

        var user: Triple<String, String, String>? = null
        if (cursor.moveToFirst()) {
            val ten = cursor.getString(0)
            val tenRole = cursor.getString(1)
            val email = cursor.getString(2) ?: "" // Nếu email NULL, gán giá trị rỗng
            user = Triple(ten, tenRole, email)
        }
        cursor.close()
        return user
    }

}
