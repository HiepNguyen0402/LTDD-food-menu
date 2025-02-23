package com.example.halidao.datmon

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.nhanvien.OrderDetailActivity

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTongTien: TextView
    private lateinit var btnConfirmOrder: Button
    private val orderViewModel: OrderViewModel by viewModels()
    private var tableNumber: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_order)

        recyclerView = findViewById(R.id.recyclerViewCart)
        tvTongTien = findViewById(R.id.tvTotalPrice)
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Nhận số bàn từ Intent
        tableNumber = intent.getStringExtra("TABLE_NUMBER")?.toIntOrNull() ?: -1
        val orderId = intent.getIntExtra("ORDER_ID", -1)
        val dbHelper = DatabaseHelper(this)
        if (orderId != -1 && dbHelper.isOrderPaid(orderId)) {
            Toast.makeText(this, "Đơn hàng này đã thanh toán, không thể thêm món!", Toast.LENGTH_SHORT).show()
            finish() // Đóng activity
            return
        }

        Log.d("OrderViewModelcart", "ID Đơn Hàng: $orderId")
        orderViewModel.loadCart() // Load danh sách giỏ hàng

        orderViewModel.selectedItems.observe(this) { items ->
            val adapter = CartAdapter(items.toMutableList()) { gioHangItem ->
                orderViewModel.removeItem(gioHangItem)
            }
            recyclerView.adapter = adapter

            val tongTien = items.sumOf { it.soTien * it.soLuong }
            tvTongTien.text = "Tổng tiền: ${tongTien} VNĐ"
        }

        // Xác nhận đơn hàng
        btnConfirmOrder.setOnClickListener {
            if (tableNumber == -1) {
                Toast.makeText(this, "Lỗi: Không tìm thấy số bàn!", Toast.LENGTH_SHORT).show()
            } else {
                val orderId = intent.getIntExtra("ORDER_ID", -1)
                val dbHelper = DatabaseHelper(this)

                // ✅ Xác nhận đơn hàng
                orderViewModel.placeOrder(tableNumber, orderId)
                Toast.makeText(this, "Đơn hàng đã được gửi!", Toast.LENGTH_SHORT).show()

                // ✅ Chuyển sang màn hình chi tiết đơn hàng
                val intent = Intent(this, OrderDetailActivity::class.java)
                intent.putExtra("ORDER_ID", orderId)
                intent.putExtra("ID_BAN", tableNumber) // Truyền số bàn

                startActivity(intent) // Mở OrderDetailActivity
                finish() // Đóng CartActivity để tránh quay lại không cần thiết
            }
        }

    }
}
