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
        viewModelScope.launch(Dispatchers.IO) {
            val cartItems = dbHelper.getCartItems()
            withContext(Dispatchers.Main) {
                _selectedItems.value = cartItems
            }
        }
    }

    fun updateItem(monAn: MenuItem, soLuong: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingItem = dbHelper.getCartItemById(monAn.id) // Kiểm tra xem món đã có trong DB chưa
            if (existingItem != null) {
                if (soLuong > 0) {
                    dbHelper.updateCartItem(monAn.id, soLuong) // Cập nhật số lượng thay vì cộng dồn
                } else {
                    dbHelper.removeFromCart(monAn.id) // Nếu số lượng về 0 thì xóa
                }
            } else {
                dbHelper.addToCart(monAn, soLuong) // Nếu chưa có, thêm mới
            }

            // Cập nhật giỏ hàng ngay lập tức
            loadCart()
        }
    }

    fun removeItem(cartItem: GioHangItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.removeFromCart(cartItem.idMonAn)
            loadCart() // Cập nhật lại LiveData để UI phản ánh thay đổi
        }
    }

    fun clearOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.clearCart()
            withContext(Dispatchers.Main) {
                _selectedItems.value = emptyList() // Xóa sạch UI
            }
        }
    }
    fun getQuantity(monAn: MenuItem): Int {
        val cartItems = _selectedItems.value ?: emptyList()
        return cartItems.find { it.idMonAn == monAn.id }?.soLuong ?: 0
    }
    fun placeOrder(idBan: Int, idDonHang: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ngayHienTai = System.currentTimeMillis()
                val items = selectedItems.value ?: emptyList()

                if (items.isEmpty()) {
                    Log.e("OrderViewModel", "Không có món ăn trong giỏ hàng")
                    return@launch
                }

                val tongTien = items.sumOf { it.soTien * it.soLuong }
                var newIdDonHang = idDonHang // Khai báo biến `var` để có thể thay đổi giá trị

                if (newIdDonHang == -1) {
                    newIdDonHang = dbHelper.insertDonHang(idBan, ngayHienTai, tongTien).toInt()
                }

                if (newIdDonHang != -1) {
                    Log.d("OrderViewModel1", "ID Đơn Hàng: $idDonHang")
                    Log.d("OrderViewModel1", "ID Đơn Hàng: $newIdDonHang")
                    items.forEach { item ->
                        dbHelper.insertChiTietDonHang(newIdDonHang, item.idMonAn, item.soLuong, item.soTien)
                    }
                    
                    // update trạng thái bàn
                    if(idDonHang == -1){
                        dbHelper.updateTableStatus(idBan, 2)
                    }
                    // Bước 3: Xóa giỏ hàng
                    dbHelper.clearCart()

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
