package com.example.halidao.nhanvien

import DatabaseHelper
import OrderDetail
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.google.android.material.tabs.TabLayout
import com.example.halidao.datmon.FoodActivity

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAction: Button
    private lateinit var btnAddFood: Button
    private lateinit var dbHelper: DatabaseHelper
    private var orderId: Int = -1
    private lateinit var adapter: OrderDetailAdapter
    private var idBan: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        dbHelper = DatabaseHelper(this)
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerViewOrderDetail)
        btnAction = findViewById(R.id.btnAction)
        btnAddFood = findViewById(R.id.btnAddFood)
        orderId = intent.getIntExtra("ORDER_ID", -1)
        idBan = intent.getIntExtra("ID_BAN", -1)

        recyclerView.layoutManager = LinearLayoutManager(this)

        tabLayout.addTab(tabLayout.newTab().setText("Chưa làm")) // Trạng thái = 4
        tabLayout.addTab(tabLayout.newTab().setText("Đang làm")) // Trạng thái = 5
        tabLayout.addTab(tabLayout.newTab().setText("Đã xong"))  // Trạng thái = 6

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadOrderDetails(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        if (orderId != -1) {
            loadOrderDetails(0) // Mặc định hiển thị "Chưa làm"
        }

        btnAction.setOnClickListener {
            when (tabLayout.selectedTabPosition) {
                0 -> updateOrderStatus(4, 5) // Chuyển từ "Chưa làm" -> "Đang làm"
                1 -> updateOrderStatus(5, 6) // Chuyển từ "Đang làm" -> "Đã xong"
                2 -> processPayment() // Thanh toán khi ở tab "Đã xong"
            }
        }
        btnAddFood.setOnClickListener {
            val tableNumber = intent.getIntExtra("ID_BAN",-1)
            val intent = Intent(this, FoodActivity::class.java) // Chuyển sang FoodActivity
            intent.putExtra("TABLE_NUMBER", tableNumber.toString())  // Truyền số bàn qua FoodActivity
            startActivity(intent)  // Chuyển Activity
        }
    }

    private fun loadOrderDetails(status: Int) {
        if (orderId == -1) return

        val statusId = when (status) {
            0 -> 4 // Chưa làm
            1 -> 5 // Đang làm
            2 -> 6 // Đã xong
            else -> 4
        }

        val items: List<OrderDetail> = dbHelper.getOrderDetailsByStatus(orderId, statusId)
        adapter = OrderDetailAdapter(items)
        recyclerView.adapter = adapter

        btnAction.visibility = if (items.isNotEmpty()) View.VISIBLE else View.GONE

        when (status) {
            0 -> btnAction.text = "Chuyển sang Đang làm"
            1 -> btnAction.text = "Chuyển sang Đã xong"
            2 -> btnAction.text = "Thanh toán"
        }
    }



    private fun updateOrderStatus(currentStatus: Int, newStatus: Int) {
        val success = dbHelper.updateOrderStatus(orderId, currentStatus, newStatus)
        if (success) {
            loadOrderDetails(tabLayout.selectedTabPosition)
        } else {
            Toast.makeText(this, "Không thể cập nhật trạng thái!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processPayment() {
        if (idBan == -1) {
            Toast.makeText(this, "Lỗi: Không xác định được bàn!", Toast.LENGTH_SHORT).show()
            return
        }

        val updated = dbHelper.updateOrderAsPaid(orderId)
        if (!updated) {
            Toast.makeText(this, "Lỗi: Không thể cập nhật trạng thái thanh toán!", Toast.LENGTH_SHORT).show()
            return
        }

        // Xóa chi tiết đơn hàng cũ để tránh lỗi hiển thị
        dbHelper.deleteOrderDetails(orderId)

        // ✅ Cập nhật trạng thái bàn về "Trống"
        dbHelper.updateTableStatus(idBan, 1)

        // ✅ Chuyển về màn hình danh sách đơn hàng
        val intent = Intent(this, OrderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }




}
