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
    private var selectedOrderDetailId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        dbHelper = DatabaseHelper(this)
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerViewOrderDetail)
        btnAction = findViewById(R.id.btnAction)
        btnAddFood = findViewById(R.id.btnAddFood)

        // ✅ Đảm bảo lấy đúng idBan từ Intent
        idBan = intent.getIntExtra("ID_BAN", -1)
        if (idBan == -1) {
            Toast.makeText(this, "Lỗi: Không xác định được bàn!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ✅ Chỉ lấy orderId một lần
        val latestOrderId = dbHelper.getLatestUnpaidOrder(idBan)
        if (latestOrderId != orderId) {
            Log.d("OrderDetailActivity", "🔄 Cập nhật orderId từ $orderId thành $latestOrderId")
            orderId = latestOrderId
        }

        Log.d("OrderDetailActivity", "🎯 Bàn: $idBan, Order ID: $orderId")

        recyclerView.layoutManager = LinearLayoutManager(this)

        tabLayout.addTab(tabLayout.newTab().setText("Chưa làm"))
        tabLayout.addTab(tabLayout.newTab().setText("Đang làm"))
        tabLayout.addTab(tabLayout.newTab().setText("Đã xong"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                loadOrderDetails(position)
                btnAddFood.visibility = if (position == 0) View.VISIBLE else View.GONE
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        if (orderId != -1) {
            loadOrderDetails(0)
        }

        btnAction.setOnClickListener {
            when (tabLayout.selectedTabPosition) {
                0, 1 -> {
                    selectedOrderDetailId?.let { orderDetailId ->
                        val currentStatus = if (tabLayout.selectedTabPosition == 0) 4 else 5
                        val newStatus = if (currentStatus == 4) 5 else 6
                        updateOrderStatus(currentStatus, newStatus, orderDetailId)
                    } ?: Toast.makeText(this, "Hãy chọn một món trước!", Toast.LENGTH_SHORT).show()
                }
                2 -> processPayment()
            }
        }

        btnAddFood.setOnClickListener {
            Log.d("OrderDetailActivity", "Bấm nút thêm món, sử dụng orderId: $orderId, bàn: $idBan")

            if (orderId == -1) {
                Toast.makeText(this, "Không tìm thấy đơn hàng hợp lệ!", Toast.LENGTH_SHORT).show()

            }

            val intent = Intent(this, FoodActivity::class.java)
            intent.putExtra("TABLE_NUMBER", idBan.toString())  // ✅ Đảm bảo bàn được truyền đúng
            intent.putExtra("ORDER_ID", orderId) // ✅ Đảm bảo truyền đúng orderId
            startActivity(intent)
        }
    }
    private fun loadOrderDetails(status: Int) {
        val statusId = when (status) {
            0 -> 4
            1 -> 5
            2 -> 6
            else -> 4
        }

        Log.d("OrderDetailActivity", "🔎 Đang tải món ăn cho orderId: $orderId, bàn: $idBan, trạng thái: $statusId")

        val items: List<OrderDetail> = dbHelper.getOrderDetailsByStatus(orderId, statusId)

        Log.d("OrderDetailActivity", "📌 Số lượng món lấy được: ${items.size}")

        adapter = OrderDetailAdapter(items) { orderDetailId ->
            selectedOrderDetailId = orderDetailId
            btnAction.visibility = if (statusId == 4 || statusId == 5) View.VISIBLE else View.GONE
        }
        recyclerView.adapter = adapter
        // ✅ Kiểm tra xem tất cả món có phải đều là "Đã xong" không
        val allCompleted = dbHelper.areAllItemsCompleted(orderId)

// ✅ Hiển thị nút "Thanh toán" nếu tất cả món đã hoàn thành
        btnAction.visibility = if (statusId == 6 && allCompleted) View.VISIBLE else View.GONE


        // Cập nhật nội dung nút dựa theo trạng thái
        when (status) {
            0 -> btnAction.text = "Chuyển sang Đang làm"
            1 -> btnAction.text = "Chuyển sang Đã xong"
            2 -> btnAction.text = "Thanh toán"
        }
    }


    private fun updateOrderStatus(currentStatus: Int, newStatus: Int, orderDetailId: Int) {
        val success = dbHelper.updateOrderStatus(orderId, currentStatus, newStatus, orderDetailId)
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

        // ✅ Cập nhật đơn hàng cũ là "Đã thanh toán"
        val updated = dbHelper.updateOrderAsPaid(orderId)
        if (!updated) {
            Toast.makeText(this, "Lỗi: Không thể cập nhật trạng thái thanh toán!", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Cập nhật trạng thái bàn thành "Trống"
        dbHelper.updateTableStatus(idBan, 1)

        // ✅ Tạo đơn hàng mới cho bàn này
        val timestamp = System.currentTimeMillis() / 1000 // Chuyển thành UNIX timestamp
        val newOrderId = dbHelper.insertOrder(idBan, timestamp, 0, 2)

        if (newOrderId != -1L) {
            Log.d("OrderDetailActivity", "✅ Đã tạo đơn hàng mới với ID: $newOrderId")

            // ✅ Cập nhật lại orderId mới
            orderId = newOrderId.toInt()

            // ✅ Load lại danh sách món ăn với đơn hàng mới
            loadOrderDetails(0)
        } else {
            Log.e("OrderDetailActivity", "❌ Không thể tạo đơn hàng mới!")
        }



        // ✅ Quay về `OrderActivity`
        val intent = Intent(this, OrderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

}
