package com.example.halidao.datmon

import DatabaseHelper
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.MenuItem

class FoodActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foodList: List<MenuItem>
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tvTableNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        // Nhận số bàn từ Intent
        val tableNumber = intent.getStringExtra("TABLE_NUMBER") ?: "?"

        // Ánh xạ TextView hiển thị số bàn
        tvTableNumber = findViewById(R.id.tvTableNumber)
        tvTableNumber.text = "Bàn số: $tableNumber"

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseHelper = DatabaseHelper(this)

        // Lấy danh sách món ăn từ cơ sở dữ liệu
        foodList = databaseHelper.getAllMenuItems()

        // Tạo adapter và gán cho RecyclerView
        foodAdapter = FoodAdapter(this, foodList)
        recyclerView.adapter = foodAdapter
    }
}

