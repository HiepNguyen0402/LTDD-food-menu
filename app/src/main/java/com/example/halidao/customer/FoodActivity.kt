package com.example.halidao.customer

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.MenuItem

class FoodActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foodList: MutableList<MenuItem>
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tvTableNumber: TextView
    private lateinit var btnGoToViewOrder: Button

    private val selectedItems = mutableListOf<MenuItem>() // Danh sách món đã chọn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        // Nhận số bàn từ Intent
        val tableNumber = intent.getStringExtra("TABLE_NUMBER") ?: "?"

        // Ánh xạ TextView hiển thị số bàn
        tvTableNumber = findViewById(R.id.tvTableNumber)
        tvTableNumber.text = "Bàn số: $tableNumber"

        recyclerView = findViewById(R.id.recyclerViewFood)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnGoToViewOrder = findViewById(R.id.btnGoToViewOrder)

        databaseHelper = DatabaseHelper(this)

        // Lấy danh sách món ăn từ cơ sở dữ liệu
        foodList = databaseHelper.getAllMenuItems().toMutableList()


        // Tạo adapter với sự kiện click
        foodAdapter = FoodAdapter(this, foodList, object : FoodAdapter.OnItemClickListener {
            override fun onAddToOrder(menuItem: MenuItem) {
                selectedItems.add(menuItem)
                Toast.makeText(this@FoodActivity, "${menuItem.tenMon} đã thêm vào đơn hàng", Toast.LENGTH_SHORT).show()
            }
        })
        recyclerView.adapter = foodAdapter

        // Xử lý khi nhấn nút "Xem đơn hàng"
        btnGoToViewOrder.setOnClickListener {
            val intent = Intent(this, OrderFoodActivity::class.java)
            intent.putParcelableArrayListExtra("ORDER_LIST", ArrayList(selectedItems))
            startActivity(intent)
        }

    }
}
