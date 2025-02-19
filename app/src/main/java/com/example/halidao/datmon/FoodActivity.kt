package com.example.halidao.datmon

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R

class FoodActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tvTableNumber: TextView
    private val orderViewModel: OrderViewModel by viewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_food)

            // Lấy số bàn từ Intent
            val tableNumber = intent.getStringExtra("TABLE_NUMBER")
            if (tableNumber.isNullOrEmpty()) {
                // Nếu không có số bàn, thông báo lỗi và đóng Activity
                finish()
                return
            }

            // Ánh xạ TextView hiển thị số bàn
            tvTableNumber = findViewById(R.id.tvTableNumber)
            tvTableNumber.text = "Bàn số: $tableNumber"

            recyclerView = findViewById(R.id.recyclerViewFood)
            recyclerView.layoutManager = LinearLayoutManager(this)

            databaseHelper = DatabaseHelper(this)

            // Lấy danh sách món ăn từ SQLite
            val menuList = databaseHelper.getAllMenuItems()
            val adapter = FoodAdapter(
                menuList,
                getQuantity = { monAn -> orderViewModel.getQuantity(monAn) },  // Lấy số lượng từ ViewModel
                onQuantityChanged = { monAn, soLuong ->
                    orderViewModel.updateItem(monAn, soLuong)
                }
            )
            recyclerView.adapter = adapter

            // Nút xem giỏ hàng
            findViewById<Button>(R.id.btnGoToViewOrder).setOnClickListener {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("TABLE_NUMBER", tableNumber) // Truyền số bàn sang CartActivity
                startActivity(intent)
            }
        }

}
