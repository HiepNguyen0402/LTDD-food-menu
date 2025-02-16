package com.example.halidao

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.example.halidao.admin.ManageActivity
import com.example.halidao.datmon.EnterTableNumberActivity
import com.example.halidao.nhanvien.OrderActivity

import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogout: Button
    private lateinit var btnViewMenuList: Button
    // Admin
    private lateinit var btnManageFood: Button
    // Nhân viên
    private lateinit var btnViewOrders: Button
    // Khách hàng

    private lateinit var btnManage: Button

    private var userRole: String? = null  // Lưu vai trò của người dùng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnManage = findViewById(R.id.btn_manage)

        // Ánh xạ các button từ XML
//        btnLogin = findViewById(R.id.btn_login)
        btnLogout = findViewById(R.id.btn_logout)
        // Admin
        btnManageFood = findViewById(R.id.btn_manage_food)
        // Nhân viên
        btnViewOrders = findViewById(R.id.btnViewOrders)
        // Khách hàng
        btnViewMenuList = findViewById(R.id.btn_view_menu_list)
        // check login
        checkUserSession()
        // Xử lý khi bấm vào các nút
        setupClickListeners()
    }

    private fun checkUserSession() {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val name = sharedPref.getString("name", null)
        val role = sharedPref.getString("role", null)
        print("check")
        val tvUserInfo = findViewById<TextView>(R.id.tv_user_info)
        if (!name.isNullOrEmpty() && !role.isNullOrEmpty()) {
            userRole = role
            tvUserInfo.text = "Tên: $name | Vai trò: $role"
            updateUIBasedOnRole()
        } else {
            userRole = null
            updateUIBasedOnRole()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    private fun updateUIBasedOnRole() {
        when (userRole) {
            "Quản lý" -> {
                // Admin thấy tất cả
                btnManageFood.visibility = View.VISIBLE
                btnManage.visibility = View.VISIBLE
                btnViewMenuList.visibility = View.VISIBLE
                btnViewOrders.visibility = View.VISIBLE

            }
            "Nhân viên" -> {
                // Nhân viên chỉ thấy "Xử lý đơn hàng"
                btnManageFood.visibility = View.GONE
                btnManage.visibility = View.GONE
                btnViewMenuList.visibility = View.GONE
                btnViewOrders.visibility = View.VISIBLE // Chỉ hiện nút này
            }
            else -> {
                // Khách hàng chỉ thấy "Xem thực đơn" và "Đặt món tại bàn"
                btnManageFood.visibility = View.GONE
                btnManage.visibility = View.GONE
                btnViewMenuList.visibility = View.GONE
                btnViewOrders.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        btnLogout.setOnClickListener {
            // Xóa dữ liệu SharedPreferences
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            // Hiển thị thông báo đăng xuất
            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()

            // Chuyển đến màn hình đăng nhập
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Xóa tất cả activity trước đó
            startActivity(intent)
        }
        btnViewOrders.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
        btnViewMenuList.setOnClickListener {
            val intent = Intent(this, EnterTableNumberActivity::class.java)
            startActivity(intent)
        }
        btnManage.setOnClickListener {
            startActivity(Intent(this, ManageActivity::class.java))
        }

    }
}
