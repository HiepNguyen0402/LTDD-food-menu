package com.example.halidao

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button

    // Admin
    private lateinit var btnManageFood: Button
    private lateinit var btnManageStaff: Button
    private lateinit var btnRevenue: Button

    // Nhân viên
    private lateinit var btnUpdateStatus: Button
    private lateinit var btnConfirmPayment: Button
    private lateinit var btnViewOrders: Button

    // Khách hàng
    private lateinit var btnViewMenu: Button
    private lateinit var btnOrderAtTable: Button
    private lateinit var btnViewOrderStatus: Button

    private var userRole: String? = null  // Lưu vai trò của người dùng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ các button từ XML
        btnLogin = findViewById(R.id.btn_login)

        // Admin
        btnManageFood = findViewById(R.id.btn_manage_food)
        btnManageStaff = findViewById(R.id.btn_manage_staff)
        btnRevenue = findViewById(R.id.btn_revenue)

        // Nhân viên
        btnUpdateStatus = findViewById(R.id.btn_update_status)
        btnConfirmPayment = findViewById(R.id.btn_confirm_payment)
        btnViewOrders = findViewById(R.id.btnViewOrders)

        // Khách hàng
        btnViewMenu = findViewById(R.id.btn_view_menu)
        btnOrderAtTable = findViewById(R.id.btn_order_at_table)
        btnViewOrderStatus = findViewById(R.id.btn_view_order_status)

        // Lấy role của người dùng từ database
        getUserRole()

        // Xử lý khi bấm vào các nút
        setupClickListeners()
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
                btnManageStaff.visibility = View.VISIBLE
                btnRevenue.visibility = View.VISIBLE
                btnUpdateStatus.visibility = View.GONE
                btnConfirmPayment.visibility = View.GONE
                btnViewOrders.visibility = View.VISIBLE
                btnViewMenu.visibility = View.GONE
                btnOrderAtTable.visibility = View.GONE
                btnViewOrderStatus.visibility = View.GONE
            }
            "staff" -> {
                btnManageFood.visibility = View.GONE
                btnManageStaff.visibility = View.GONE
                btnRevenue.visibility = View.GONE
                btnUpdateStatus.visibility = View.VISIBLE
                btnConfirmPayment.visibility = View.VISIBLE
                btnViewOrders.visibility = View.VISIBLE
                btnViewMenu.visibility = View.GONE
                btnOrderAtTable.visibility = View.GONE
                btnViewOrderStatus.visibility = View.GONE
            }
            "customer" -> {
                btnManageFood.visibility = View.GONE
                btnManageStaff.visibility = View.GONE
                btnRevenue.visibility = View.GONE
                btnUpdateStatus.visibility = View.GONE
                btnConfirmPayment.visibility = View.GONE
                btnViewMenu.visibility = View.VISIBLE
                btnOrderAtTable.visibility = View.VISIBLE
                btnViewOrderStatus.visibility = View.VISIBLE
                btnViewOrders.visibility = View.VISIBLE

            }
            else -> {
                // Nếu chưa đăng nhập, ẩn hết nút trừ đăng nhập
                btnManageFood.visibility = View.GONE
                btnManageStaff.visibility = View.GONE
                btnRevenue.visibility = View.GONE
                btnUpdateStatus.visibility = View.GONE
                btnConfirmPayment.visibility = View.GONE
                btnViewOrders.visibility = View.VISIBLE
                btnViewMenu.visibility = View.GONE
                btnOrderAtTable.visibility = View.GONE
                btnViewOrderStatus.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnViewOrders.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }

//        btnManageFood.setOnClickListener {
//            startActivity(Intent(this, ManageFoodActivity::class.java))
//        }
//
//        btnManageStaff.setOnClickListener {
//            startActivity(Intent(this, ManageStaffActivity::class.java))
//        }
//
//        btnRevenue.setOnClickListener {
//            startActivity(Intent(this, RevenueActivity::class.java))
//        }
//
//        btnProcessOrder.setOnClickListener {
//            startActivity(Intent(this, ProcessOrderActivity::class.java))
//        }
//
//        btnUpdateStatus.setOnClickListener {
//            startActivity(Intent(this, UpdateStatusActivity::class.java))
//        }
//
//        btnConfirmPayment.setOnClickListener {
//            startActivity(Intent(this, ConfirmPaymentActivity::class.java))
//        }
//
//        btnViewMenu.setOnClickListener {
//            startActivity(Intent(this, ViewMenuActivity::class.java))
//        }
//
//        btnOrderAtTable.setOnClickListener {
//            startActivity(Intent(this, OrderAtTableActivity::class.java))
//        }
//
//        btnViewOrderStatus.setOnClickListener {
//            startActivity(Intent(this, ViewOrderStatusActivity::class.java))
//        }
    }
}
