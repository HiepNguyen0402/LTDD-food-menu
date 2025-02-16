package com.example.halidao.customer

import OrderFoodAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.MenuItem
import com.example.halidao.data.model.OrderItem

class OrderFoodActivity : AppCompatActivity() {
    private lateinit var adapter: OrderFoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_order)

        val recyclerViewOrder = findViewById<RecyclerView>(R.id.recyclerViewCart)

        // Nhận danh sách món ăn từ Intent
        val orderList = intent.getParcelableArrayListExtra<MenuItem>("ORDER_LIST")?.map {
            OrderItem(it.tenMon, 1, it.gia) // Mặc định số lượng là 1
        }?.toMutableList() ?: mutableListOf()

        // Hiển thị danh sách trong RecyclerView
        val adapter = OrderFoodAdapter(orderList) { orderItem ->
            orderList.remove(orderItem)  // Xóa món ăn khỏi danh sách
            adapter.notifyDataSetChanged() // Cập nhật danh sách
        }

        recyclerViewOrder.layoutManager = LinearLayoutManager(this)
        recyclerViewOrder.adapter = adapter
    }
}

