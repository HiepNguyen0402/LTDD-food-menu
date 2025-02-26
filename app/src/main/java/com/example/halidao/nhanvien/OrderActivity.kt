package com.example.halidao.nhanvien

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.google.android.material.tabs.TabLayout

class OrderActivity : AppCompatActivity() {
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var dataHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        dataHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewOrders)
        tabLayout = findViewById(R.id.tabLayout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Thêm tab cho "Bàn trống" và "Bàn đang sử dụng"
        tabLayout.addTab(tabLayout.newTab().setText("Bàn trống"))       // id_trang_thai = 1
        tabLayout.addTab(tabLayout.newTab().setText("Bàn đang sử dụng")) // id_trang_thai = 2

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadOrders(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        loadOrders(0) // Mặc định hiển thị "Bàn trống"
    }

    private fun loadOrders(tabIndex: Int) {
        val statusId = if (tabIndex == 0) 1 else 2 // 1 = Bàn trống, 2 = Đang sử dụng
        val orders = dataHelper.getOrdersByTableStatus(statusId)

        orderAdapter = OrderAdapter(
            orders,
            onUpdateStatus = { order ->
                val updated = dataHelper.updateTableStatus(order.idBan, 2) // Chuyển bàn sang trạng thái "Đang sử dụng"
                if (updated) {
                    Toast.makeText(this, "Cập nhật trạng thái bàn thành công", Toast.LENGTH_SHORT).show()
                    loadOrders(tabLayout.selectedTabPosition)
                }
            },
            onPayment = { order ->
                val success = dataHelper.payOrder(order.id, order.tongTien, "Tiền mặt")
                if (success) {
                    dataHelper.deleteOrderDetails(order.id) // Xóa chi tiết đơn hàng cũ
                    dataHelper.deleteOldOrdersForTable(order.idBan)
                    dataHelper.updateTableStatus(order.idBan, 1)
                    dataHelper.insertOrder(order.idBan, null, 0, 1)

                    loadOrders(tabLayout.selectedTabPosition)
                }
            },
            onClickOrder = { order ->
                val intent = Intent(this, OrderDetailActivity::class.java)
                intent.putExtra("ORDER_ID", order.id)
                intent.putExtra("ID_BAN", order.idBan)
                startActivity(intent)
            }
        )

        recyclerView.adapter = orderAdapter
    }
}
