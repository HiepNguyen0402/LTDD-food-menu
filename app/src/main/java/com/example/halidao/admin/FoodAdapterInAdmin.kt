package com.example.halidao.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.MenuItem

class FoodAdapterInAdmin(
    private val context: Context,
    private var foodList: MutableList<MenuItem>,
    private val onFoodSelected: (MenuItem) -> Unit,
    private val onFoodDeleted: (Int) -> Unit
) : RecyclerView.Adapter<FoodAdapterInAdmin.FoodViewHolder>() {

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFoodName: TextView = itemView.findViewById(R.id.txtFoodName)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_simple, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = foodList[position]
        holder.txtFoodName.text = item.tenMon

        holder.itemView.setOnClickListener {
            onFoodSelected(item)
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Xóa món ăn")
                .setMessage("Bạn có chắc muốn xóa món '${item.tenMon}'?")
                .setPositiveButton("Xóa") { _, _ ->
                    onFoodDeleted(item.id)
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    override fun getItemCount(): Int = foodList.size
}
