package com.example.halidao.customer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.halidao.R
import com.example.halidao.data.model.MenuItem

class FoodAdapter(
    private val context: Context,
    private val foodList: List<MenuItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    interface OnItemClickListener {
        fun onAddToOrder(menuItem: MenuItem) // Xử lý khi nhấn nút "Thêm vào đơn"
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val foodPrice: TextView = itemView.findViewById(R.id.foodPrice)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        val btnAddToOrder: Button = itemView.findViewById(R.id.btnSelectFood) // Nút "Thêm vào đơn"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.foodName.text = foodItem.tenMon
        holder.foodPrice.text = "${foodItem.gia} VND"
        Glide.with(context).load(foodItem.hinhAnh).into(holder.foodImage)

        // Xử lý khi bấm vào nút "Thêm vào đơn"
        holder.btnAddToOrder.setOnClickListener {
            listener.onAddToOrder(foodItem)
        }
    }

    override fun getItemCount(): Int = foodList.size
}
