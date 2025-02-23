package com.example.halidao.nhanvien

import Order
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R

class OrderAdapter(
    private var orders: List<Order>,
    private val onUpdateStatus: (Order) -> Unit,
    private val onPayment: (Order) -> Unit,
    private val onClickOrder: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTable: TextView = itemView.findViewById(R.id.txtTable)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
        val btnUpdateStatus: Button = itemView.findViewById(R.id.btnUpdateStatus)
        val btnPayment: Button = itemView.findViewById(R.id.btnPayment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // Thêm log để kiểm tra trạng thái bàn
        Log.d("OrderAdapter", "Bàn ${order.idBan} - Trạng thái: ${order.trangThai}")

        holder.txtTable.text = "Bàn: ${order.idBan}"
        holder.txtTotal.text = "Tổng tiền: ${order.tongTien} VND"

        // Hiển thị trạng thái bàn
        holder.txtStatus.text = if (order.trangThai == 1) "Trống" else "Đang sử dụng"

        // Kiểm tra xem nút cập nhật có bị ẩn sai không
        holder.btnUpdateStatus.visibility = if (order.trangThai == 1) View.VISIBLE else View.GONE

        // Ẩn nút thanh toán nếu bàn trống
        holder.btnPayment.visibility = if (order.trangThai == 2) View.VISIBLE else View.GONE

        holder.btnUpdateStatus.setOnClickListener {
            Log.d("OrderAdapter", "Cập nhật trạng thái bàn ${order.idBan}")
            onUpdateStatus(order)
        }

        holder.btnPayment.setOnClickListener { onPayment(order) }
        holder.itemView.setOnClickListener { onClickOrder(order) }
    }


    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun getItemCount() = orders.size
}
