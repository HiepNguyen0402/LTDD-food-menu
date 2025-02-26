package com.example.halidao.nhanvien

import OrderDetail
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R

class OrderDetailAdapter(private val orderDetails: List<OrderDetail>,
                         private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {

    inner class OrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDishName: TextView = itemView.findViewById(R.id.txtDishNameDetail)
        val txtQuantity: TextView = itemView.findViewById(R.id.txtQuantity)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(orderDetails[position].orderDetailId) // Gửi ID của món được chọn
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
        return OrderDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val detail = orderDetails[position]
        holder.txtDishName.text = detail.tenMon
        holder.txtQuantity.text = "Số lượng: ${detail.soLuong}"
        holder.txtPrice.text = "Giá: ${detail.gia} VND"
    }

    override fun getItemCount() = orderDetails.size
}
