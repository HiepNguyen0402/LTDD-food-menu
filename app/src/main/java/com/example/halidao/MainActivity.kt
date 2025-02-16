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
//    private lateinit var btnLogin: Button
    private lateinit var btnLogout: Button
    private lateinit var btnViewMenuList: Button
    // Admin
    private lateinit var btnManageFood: Button
//    private lateinit var btnRevenue: Button


    // Nhân viên
    private lateinit var btnUpdateStatus: Button
    private lateinit var btnConfirmPayment: Button
    private lateinit var btnViewOrders: Button

    // Khách hàng
    private lateinit var btnViewMenu: Button
    private lateinit var btnOrderAtTable: Button
    private lateinit var btnViewOrderStatus: Button
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
        btnViewOrderStatus = findViewById(R.id.btn_view_order_status)
        // Nhân viên
        btnUpdateStatus = findViewById(R.id.btn_update_status)
        btnConfirmPayment = findViewById(R.id.btn_confirm_payment)
        btnViewOrders = findViewById(R.id.btnViewOrders)

        // Khách hàng
        btnViewMenu = findViewById(R.id.btn_view_menu)
        btnOrderAtTable = findViewById(R.id.btn_order_at_table)
        btnViewMenuList = findViewById(R.id.btn_view_menu_list)

        // check login
        checkUserSession()
        // Lấy role của người dùng từ database
        getUserRole()

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

    private fun getUserRole() {
        // Giả sử lấy role từ database hoặc SharedPreferences
        lifecycleScope.launch {
            userRole = "admin" // Tạm gán, sau này lấy từ database
            updateUIBasedOnRole()
        }
    }

    private fun updateUIBasedOnRole() {
        when (userRole) {
            "admin" -> {
                btnManageFood.visibility = View.VISIBLE
//                btnRevenue.visibility = View.VISIBLE
                btnUpdateStatus.visibility = View.GONE
                btnConfirmPayment.visibility = View.GONE
                btnViewOrders.visibility = View.VISIBLE
                btnViewMenu.visibility = View.GONE
                btnOrderAtTable.visibility = View.GONE
                btnViewOrderStatus.visibility = View.GONE
                btnViewMenuList.visibility = View.VISIBLE
                btnManage.visibility = View.VISIBLE

            }
            "staff" -> {
                btnManageFood.visibility = View.GONE
//                btnRevenue.visibility = View.GONE
                btnUpdateStatus.visibility = View.VISIBLE
                btnConfirmPayment.visibility = View.VISIBLE
                btnViewOrders.visibility = View.VISIBLE
                btnViewMenu.visibility = View.GONE
                btnOrderAtTable.visibility = View.GONE
                btnViewOrderStatus.visibility = View.GONE
                btnViewMenuList.visibility = View.VISIBLE
                btnManage.visibility = View.VISIBLE

            }
            "customer" -> {
                btnManageFood.visibility = View.GONE
//                btnRevenue.visibility = View.GONE
                btnUpdateStatus.visibility = View.GONE
                btnConfirmPayment.visibility = View.GONE
                btnViewMenu.visibility = View.VISIBLE
                btnOrderAtTable.visibility = View.VISIBLE
                btnViewOrderStatus.visibility = View.VISIBLE
                btnViewOrders.visibility = View.VISIBLE
//                btnViewMenuList.visibility = View.VISIBLE
                btnManage.visibility = View.VISIBLE

            }
            else -> {
                // Nếu chưa đăng nhập, ẩn hết nút trừ đăng nhập
                btnManageFood.visibility = View.GONE
//                btnRevenue.visibility = View.GONE
                btnUpdateStatus.visibility = View.GONE
                btnConfirmPayment.visibility = View.GONE
                btnViewOrders.visibility = View.VISIBLE
                btnViewMenu.visibility = View.GONE
                btnOrderAtTable.visibility = View.GONE
                btnViewOrderStatus.visibility = View.GONE
                btnViewMenuList.visibility = View.VISIBLE
                btnManage.visibility = View.VISIBLE
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
