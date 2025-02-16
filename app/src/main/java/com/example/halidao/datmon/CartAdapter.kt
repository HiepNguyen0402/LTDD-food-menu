package com.example.halidao.datmon

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.GioHangItem

class CartAdapter(
    private val cartList: MutableList<GioHangItem>, // Dùng MutableList để có thể xóa
    private val onRemoveFromCart: (GioHangItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenMon: TextView = itemView.findViewById(R.id.tvTenMon)
        val gia: TextView = itemView.findViewById(R.id.tvGia)
        val soLuong: TextView = itemView.findViewById(R.id.tvSoLuong)
        val btnXoa: Button = itemView.findViewById(R.id.btnXoa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_order, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartList[position]
        holder.tenMon.text = cartItem.tenMon
        holder.gia.text = "${cartItem.soTien} VNĐ"
        holder.soLuong.text = "Số lượng: ${cartItem.soLuong}"

        holder.btnXoa.setOnClickListener {
            Log.d("CartAdapter", "Đã xoá món: ${cartItem.tenMon}, ID: ${cartItem.idMonAn}")
            onRemoveFromCart(cartItem) // Xóa khỏi database
            removeItem(position) // Xóa khỏi danh sách hiển thị
        }
    }

    override fun getItemCount(): Int = cartList.size

    fun removeItem(position: Int) {
        if (position in cartList.indices) { // Kiểm tra nếu vị trí hợp lệ
            cartList.removeAt(position) // Xóa item khỏi danh sách
            notifyItemRemoved(position) // Cập nhật RecyclerView
            notifyItemRangeChanged(position, cartList.size) // Cập nhật lại chỉ mục
        }
    }
}

