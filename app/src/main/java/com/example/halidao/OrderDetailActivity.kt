package com.example.halidao

import DatabaseHelper
import OrderDetail
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.halidao.R

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var txtOrderDetail: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        txtOrderDetail = findViewById(R.id.txtOrderDetail)
        dbHelper = DatabaseHelper(this)
        val orderId = intent.getIntExtra("ORDER_ID", -1)
        if (orderId != -1) {
            loadOrderDetails(orderId)
        }
    }
    private fun loadOrderDetails(orderId: Int) {
        val items: List<OrderDetail> = dbHelper.getOrderDetails(orderId)
        if (items.isNotEmpty()) {
            txtOrderDetail.text = items.joinToString("\n") { item ->
                "${item.tenMon} - Số lượng: ${item.soLuong} - Giá: ${item.gia} VND"
            }
        } else {
            txtOrderDetail.text = "Không có món ăn trong đơn hàng."
        }
    }

}
