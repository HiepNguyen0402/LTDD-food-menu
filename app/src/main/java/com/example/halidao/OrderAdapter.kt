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
    private val onClickOrder: (Order) -> Unit // Thêm hàm callback để mở chi tiết đơn hàng
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTable: TextView = itemView.findViewById(R.id.txtTable)
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
        holder.txtTable.text = "Bàn: ${order.idBan}"
        holder.txtTotal.text = "Tổng tiền: ${order.tongTien} VND"

        holder.btnUpdateStatus.setOnClickListener { onUpdateStatus(order) }
        holder.btnPayment.setOnClickListener { onPayment(order) }

        // Bấm vào item để xem chi tiết món ăn
        holder.itemView.setOnClickListener { onClickOrder(order) }
    }
    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun getItemCount() = orders.size
}
