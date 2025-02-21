package com.example.halidao.datmon

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.GioHangItem
import com.example.halidao.data.model.MenuItem
import java.io.File

class FoodAdapter(
    private val menuList: List<MenuItem>,
    private val getQuantity: (MenuItem) -> Int,  // Lấy số lượng hiện tại từ ViewModel
    private val onQuantityChanged: (MenuItem, Int) -> Unit
) : RecyclerView.Adapter<FoodAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenMon: TextView = itemView.findViewById(R.id.foodName)
        val gia: TextView = itemView.findViewById(R.id.foodPrice)
        val btnGiam: Button = itemView.findViewById(R.id.btnDecrease)
        val btnTang: Button = itemView.findViewById(R.id.btnIncrease)
        val soLuong: TextView = itemView.findViewById(R.id.foodQuantity)
        val imageView: ImageView = itemView.findViewById(R.id.foodImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val monAn = menuList[position]
        var quantity = getQuantity(monAn)  // Lấy số lượng hiện tại từ ViewModel

        holder.tenMon.text = monAn.tenMon
        holder.gia.text = "${monAn.gia} VNĐ"
        holder.soLuong.text = quantity.toString()

        val context = holder.itemView.context
        val imageFile = File(context.filesDir, "${monAn.hinhAnh}.png")

        if (imageFile.exists()) {
            // Nếu có trong Internal Storage
            holder.imageView.setImageURI(Uri.fromFile(imageFile))
        } else {
            // Kiểm tra trong Drawable
            val resourceId = context.resources.getIdentifier(monAn.hinhAnh, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.imageView.setImageResource(resourceId)
            } else {
                // Nếu không có, dùng ảnh mặc định
                holder.imageView.setImageResource(R.drawable.macdinh)
            }
        }

        // Cập nhật trạng thái nút "-"
        holder.btnGiam.isEnabled = quantity > 0

        holder.btnTang.setOnClickListener {
            quantity += 1
            holder.soLuong.text = quantity.toString()
            holder.btnGiam.isEnabled = true
            onQuantityChanged(monAn, quantity)  // Cập nhật số lượng chính xác
        }

        holder.btnGiam.setOnClickListener {
            if (quantity > 0) {
                quantity -= 1
                holder.soLuong.text = quantity.toString()
                holder.btnGiam.isEnabled = quantity > 0
                onQuantityChanged(monAn, quantity)  // Cập nhật số lượng chính xác
            }
        }
    }

    override fun getItemCount(): Int = menuList.size
}
