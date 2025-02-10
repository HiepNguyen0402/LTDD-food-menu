package com.example.halidao

import DatabaseHelper
import OrderAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderActivity : AppCompatActivity() {
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var dataHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        dataHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lấy danh sách đơn hàng
        val orders = dataHelper.getOrdersByStatus(1) // Trạng thái "Chưa làm"
        Log.d("OrderActivity", "Danh sách đơn hàng lấy được: $orders")

        if (orders.isEmpty()) {
            Log.d("OrderActivity", "Không có đơn hàng, thêm dữ liệu mẫu...")
            dataHelper.insertOrder(1, 100000, 1,1) // Thêm đơn hàng giả định
        }


        orderAdapter = OrderAdapter(
            orders,
            onUpdateStatus = { order ->
                val updated = dataHelper.updateOrderStatus(order.id, 2) // Chuyển sang "Đang làm"
                if (updated) {
                    Toast.makeText(this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show()
                    refreshOrders()
                }
            },
            onPayment = { order ->
                val success = dataHelper.payOrder(order.id, order.tongTien, "Tiền mặt")
                if (success) {
                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show()
                    refreshOrders()
                }
            },
            onClickOrder = { order ->
                // Xử lý khi bấm vào đơn hàng, có thể mở màn hình chi tiết đơn hàng
                val intent = Intent(this, OrderDetailActivity::class.java)
                intent.putExtra("ORDER_ID", order.id)
                startActivity(intent)
            }
        )


        recyclerView.adapter = orderAdapter
        refreshOrders()
    }

    private fun refreshOrders() {
        val newOrders = dataHelper.getOrdersByStatus(1) // Lọc theo trạng thái "Chưa làm"
        orderAdapter.updateData(newOrders)
    }

}
