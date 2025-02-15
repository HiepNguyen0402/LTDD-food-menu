import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.OrderItem

class OrderFoodAdapter(
    private val orderList: MutableList<OrderItem>,
    private val onRemoveClick: (OrderItem) -> Unit // Callback để xử lý khi xóa món
) : RecyclerView.Adapter<OrderFoodAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderItem = orderList[position]
        holder.foodName.text = orderItem.foodName
        holder.foodPrice.text = "${orderItem.getTotalPrice()} VND"

        // Xử lý sự kiện khi nhấn "Xóa"
        holder.btnRemove.setOnClickListener {
            onRemoveClick(orderItem)
            orderList.removeAt(position)  // Xóa khỏi danh sách
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, orderList.size) // Cập nhật danh sách
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodQuality: ImageView = itemView.findViewById(R.id.txtCartItemQuantity)
        val foodName: TextView = itemView.findViewById(R.id.txtCartItemName)
        val foodPrice: TextView = itemView.findViewById(R.id.txtCartItemPrice)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemoveFromCart)
    }
}
