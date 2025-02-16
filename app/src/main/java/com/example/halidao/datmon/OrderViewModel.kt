package com.example.halidao.datmon

import DatabaseHelper
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.halidao.data.model.GioHangItem
import com.example.halidao.data.model.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper(application)

    private val _selectedItems = MutableLiveData<List<GioHangItem>>()
    val selectedItems: LiveData<List<GioHangItem>> get() = _selectedItems

    fun loadCart() {
        _selectedItems.value = dbHelper.getCartItems()
    }

    fun addItem(monAn: MenuItem) {
        dbHelper.addToCart(monAn)
        loadCart() // Cập nhật lại LiveData
    }

    fun removeItem(cartItem: GioHangItem) {
        dbHelper.removeFromCart(cartItem.idMonAn)  // Xóa trong database
        val updatedList = _selectedItems.value?.toMutableList()
        updatedList?.remove(cartItem) // Xóa khỏi LiveData
//        _selectedItems.value = updatedList
    }

    fun clearOrder() {
        dbHelper.clearCart()
        loadCart()
    }
    fun placeOrder(idBan: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ngayHienTai = System.currentTimeMillis()

                // Lấy danh sách món ăn từ giỏ hàng, nếu null thì trả về danh sách rỗng
                val items = selectedItems.value ?: emptyList()

                if (items.isEmpty()) {
                    Log.e("OrderViewModel", "Không có món ăn trong giỏ hàng")
                    return@launch
                }

                val tongTien = items.sumOf { it.soTien * it.soLuong }

                // Bước 1: Thêm đơn hàng vào SQLite
                val idDonHang = dbHelper.insertDonHang(idBan, ngayHienTai, tongTien)

                if (idDonHang != -1L) {
                    // Bước 2: Thêm từng món ăn vào ChiTietDonHang
                    items.forEach { item ->
                        dbHelper.insertChiTietDonHang(idDonHang.toInt(), item.idMonAn, item.soLuong, item.soTien)
                    }
                    
                    // update trạng thái bàn
                    dbHelper.updateTableStatus(idBan, 2)
                    // Bước 3: Xóa giỏ hàng
                    dbHelper.clearCart()

                    // Bước 4: Cập nhật UI trên luồng chính
                    withContext(Dispatchers.Main) {
                        _selectedItems.value = emptyList() // Cập nhật giỏ hàng rỗng
                    }
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Lỗi khi đặt hàng: ${e.message}")
            }
        }
    }



}
