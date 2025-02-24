package com.example.halidao

import DatabaseHelper
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.adapter.FoodAdapterInAdmin
import com.example.halidao.data.model.MenuItem
import java.io.File
import java.io.IOException

class ManageFoodFragment : Fragment() {

    private lateinit var edtFoodName: EditText
    private lateinit var edtFoodPrice: EditText
    private lateinit var spinnerFoodCategory: Spinner
    private lateinit var btnSaveFood: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: FoodAdapterInAdmin
    private var selectedFoodId: Int? = null
    private lateinit var imgFood: ImageView
    private var selectedImageUri: Uri? = null
    private var categoriesList: List<Pair<Int, String>> = listOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manage_food, container, false)

        edtFoodName = view.findViewById(R.id.edtFoodName)
        edtFoodPrice = view.findViewById(R.id.edtFoodPrice)
        spinnerFoodCategory = view.findViewById(R.id.spinnerFoodCategory)
        databaseHelper = DatabaseHelper(requireContext())

// Tạo Adapter từ danh sách trong strings.xml
        // Lấy danh mục từ database
        categoriesList = databaseHelper.getAllCategories()
        val categoriesNames = categoriesList.map { it.second } // Chỉ lấy tên danh mục để hiển thị

// Tạo Adapter cho Spinner với danh mục từ database
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoriesNames)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFoodCategory.adapter = adapterSpinner


        imgFood = view.findViewById(R.id.imgFood)
        btnSaveFood = view.findViewById(R.id.btnSaveFood)
        recyclerView = view.findViewById(R.id.recyclerViewFood)
        imgFood.setOnClickListener {
            openImagePicker()
        }
        loadCategories()
        loadFoodList()

        btnSaveFood.setOnClickListener {
            saveFood()
        }

        return view
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imgFood.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
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
    private fun loadCategories() {
        categoriesList = databaseHelper.getAllCategories()

        // ✅ In log kiểm tra danh sách danh mục
        Log.d("DEBUG", "Danh sách danh mục (ID - Tên): $categoriesList")

        val adapterSpinner = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoriesList.map { it.second } // Chỉ lấy tên danh mục
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFoodCategory.adapter = adapterSpinner
    }





    private fun saveFood() {
        val name = edtFoodName.text.toString().trim()
        val price = edtFoodPrice.text.toString().trim().toIntOrNull() ?: 0

        // ✅ Lấy ID danh mục từ vị trí trong Spinner
        val selectedCategoryIndex = spinnerFoodCategory.selectedItemPosition
        val selectedCategoryId = categoriesList[selectedCategoryIndex].first

        val newImageName = selectedImageUri?.let { saveImageToInternalStorage(it) }
        val oldImageName = selectedFoodId?.let { databaseHelper.getFoodImageById(it) } ?: ""
        val finalImageName = newImageName ?: oldImageName

        if (name.isEmpty() || price <= 0 || finalImageName.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedFoodId == null) {
            databaseHelper.insertFood(MenuItem(0, name, price, finalImageName, selectedCategoryId))
            Toast.makeText(requireContext(), "Thêm món thành công!", Toast.LENGTH_SHORT).show()
        } else {
            databaseHelper.updateFood(selectedFoodId!!, name, price, finalImageName, selectedCategoryId)
            Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            selectedFoodId = null
            btnSaveFood.text = "Thêm món"
        }

        clearInputs()
        loadFoodList()
    }




    private fun saveImageToInternalStorage(uri: Uri): String {
        val fileName = getFileNameFromUri(uri).substringBeforeLast(".") // Lấy tên file không có phần mở rộng
        val file = File(requireContext().filesDir, "$fileName.png") // Lưu với định dạng PNG

        return try {
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            fileName // Trả về tên file để lưu vào database
        } catch (e: IOException) {
            Log.e("SAVE_IMAGE", "Lỗi lưu ảnh", e)
            ""
        }
    }
    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex) ?: "default_image"
        } ?: "default_image"
    }

    private fun onFoodSelected(food: MenuItem) {
        selectedFoodId = food.id
        edtFoodName.setText(food.tenMon)
        edtFoodPrice.setText(food.gia.toString())

        Log.d("ManageFoodFragment", "ID danh mục của món: ${food.danhMuc}")

        // ✅ Kiểm tra nếu ID danh mục là 0, đặt về danh mục mặc định
        val categoryId = if (food.danhMuc == 0) categoriesList.firstOrNull()?.first ?: 1 else food.danhMuc

        val categoryIndex = categoriesList.indexOfFirst { it.first == categoryId }

        if (categoryIndex != -1) {
            spinnerFoodCategory.setSelection(categoryIndex)
        } else {
            Log.e("ManageFoodFragment", "Không tìm thấy ID danh mục ($categoryId) trong categoriesList!")
            spinnerFoodCategory.setSelection(0) // Đặt về danh mục đầu tiên
        }
        val context = requireContext()
        val imageFile = File(context.filesDir, "${food.hinhAnh}.png")

        if (imageFile.exists()) {
            imgFood.setImageURI(Uri.fromFile(imageFile))
        } else {
            val resourceId = context.resources.getIdentifier(food.hinhAnh, "drawable", context.packageName)
            if (resourceId != 0) {
                imgFood.setImageResource(resourceId)
            } else {
                imgFood.setImageResource(R.drawable.macdinh)
            }
        }

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
        spinnerFoodCategory.setSelection(0) // ✅ Đặt về danh mục đầu tiên
    }

}
