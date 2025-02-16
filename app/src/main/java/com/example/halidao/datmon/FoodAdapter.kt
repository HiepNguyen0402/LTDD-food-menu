package com.example.halidao.datmon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.GioHangItem
import com.example.halidao.data.model.MenuItem

class FoodAdapter(private val menuList: List<MenuItem>,
                  private val onAddToCart: (MenuItem) -> Unit
) : RecyclerView.Adapter<FoodAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenMon: TextView = itemView.findViewById(R.id.foodName)
        val gia: TextView = itemView.findViewById(R.id.foodPrice)
        val btnThem: Button = itemView.findViewById(R.id.btnSelectFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val monAn = menuList[position]
        holder.tenMon.text = monAn.tenMon
        holder.gia.text = "${monAn.gia} VNĐ"

        holder.btnThem.setOnClickListener {
            onAddToCart(monAn) // Thêm vào giỏ hàng
        }
    }

    override fun getItemCount(): Int = menuList.size
}
