package com.example.halidao

import DatabaseHelper
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.adapter.FoodAdapterInAdmin
import com.example.halidao.data.model.MenuItem

class ManageFoodFragment : Fragment() {

    private lateinit var edtFoodName: EditText
    private lateinit var edtFoodPrice: EditText
    private lateinit var edtFoodCategory: EditText
    private lateinit var edtFoodImage: EditText
    private lateinit var btnSaveFood: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: FoodAdapterInAdmin
    private var selectedFoodId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manage_food, container, false)

        edtFoodName = view.findViewById(R.id.edtFoodName)
        edtFoodPrice = view.findViewById(R.id.edtFoodPrice)
        edtFoodCategory = view.findViewById(R.id.edtFoodCategory)
        edtFoodImage = view.findViewById(R.id.edtFoodImage)
        btnSaveFood = view.findViewById(R.id.btnSaveFood)
        recyclerView = view.findViewById(R.id.recyclerViewFood)

        databaseHelper = DatabaseHelper(requireContext())
        loadFoodList()

        btnSaveFood.setOnClickListener {
            saveFood()
        }

        return view
    }

    private fun loadFoodList() {
        val foodList = databaseHelper.getAllMenuItems().toMutableList()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FoodAdapterInAdmin(requireContext(), foodList,
            onFoodSelected = { food -> onFoodSelected(food) },
            onFoodDeleted = { foodId -> onFoodDeleted(foodId) }
        )
        recyclerView.adapter = adapter
    }

    private fun saveFood() {
        val name = edtFoodName.text.toString()
        val price = edtFoodPrice.text.toString().toIntOrNull() ?: 0
        val category = edtFoodCategory.text.toString()
        val image = edtFoodImage.text.toString()

        if (name.isEmpty() || price <= 0 || category.isEmpty() || image.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedFoodId == null) {
            databaseHelper.insertFood(MenuItem(0, name, price, image, category))
            Toast.makeText(requireContext(), "Thêm món thành công!", Toast.LENGTH_SHORT).show()
        } else {
            databaseHelper.updateFood(selectedFoodId!!, name, price)
            Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            selectedFoodId = null
            btnSaveFood.text = "Thêm món"
        }

        clearInputs()
        loadFoodList()
    }

    private fun onFoodSelected(food: MenuItem) {
        selectedFoodId = food.id
        edtFoodName.setText(food.tenMon)
        edtFoodPrice.setText(food.gia.toString())
        edtFoodCategory.setText(food.danhMuc)
        edtFoodImage.setText(food.hinhAnh)
        btnSaveFood.text = "Cập nhật món"
    }

    private fun onFoodDeleted(foodId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa món ăn")
            .setMessage("Bạn có chắc muốn xóa món này?")
            .setPositiveButton("Xóa") { _, _ ->
                databaseHelper.deleteFood(foodId)
                Toast.makeText(requireContext(), "Đã xóa món ăn!", Toast.LENGTH_SHORT).show()
                loadFoodList()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun clearInputs() {
        edtFoodName.text.clear()
        edtFoodPrice.text.clear()
        edtFoodCategory.text.clear()
        edtFoodImage.text.clear()
    }
}
