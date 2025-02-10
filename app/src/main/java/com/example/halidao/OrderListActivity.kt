package com.example.halidao

import DatabaseHelper
import Order
import OrderAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.tabs.TabLayout

class OrderListActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewOrders: RecyclerView
    private lateinit var adapter: OrderAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        dbHelper = DatabaseHelper(this)
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders)
        tabLayout = findViewById(R.id.tabLayout)

        recyclerViewOrders.layoutManager = LinearLayoutManager(this)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadOrders(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        loadOrders(0) // Mặc định hiển thị "Chưa làm"
    }
    private fun openOrderDetail(order: Order) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra("ORDER_ID", order.id)
        startActivity(intent)
    }
    private fun updateOrderStatus(order: Order) {
        val nextStatus = when (order.trangThai) {
            1 -> 2  // Chưa làm → Đang làm
            2 -> 3  // Đang làm → Đã ra món
            else -> order.trangThai
        }
        dbHelper.updateOrderStatus(order.id, nextStatus)
        loadOrders(tabLayout.selectedTabPosition) // Cập nhật lại danh sách đơn hàng theo tab hiện tại
    }

    private fun processPayment(order: Order) {
        if (order.daThanhToan) {
            Toast.makeText(this, "Đơn hàng đã thanh toán!", Toast.LENGTH_SHORT).show()
            return
        }
        dbHelper.markOrderAsPaid(order.id)
        loadOrders(tabLayout.selectedTabPosition) // Reload danh sách
    }

    private fun loadOrders(status: Int) {
        val orders = when (status) {
            0 -> dbHelper.getOrdersByStatus(1) // Chưa làm
            1 -> dbHelper.getOrdersByStatus(2) // Đang làm
            2 -> dbHelper.getOrdersByStatus(3) // Đã làm
            else -> dbHelper.getAllOrders()
        }
        adapter = OrderAdapter(orders, ::updateOrderStatus, ::processPayment, ::openOrderDetail)
        recyclerViewOrders.adapter = adapter
    }
}
