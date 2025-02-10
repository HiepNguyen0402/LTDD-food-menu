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

            if (checkLogin(username, password)) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun checkLogin(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT * FROM NhanVien 
        WHERE (sdt = ? OR email = ?) 
        AND mat_khau = ?
    """
        val cursor = db.rawQuery(query, arrayOf(username, username, password))

        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }
}
