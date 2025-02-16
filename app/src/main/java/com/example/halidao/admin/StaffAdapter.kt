package com.example.halidao.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.Staff

class StaffAdapter(
    private val context: Context,
    private val staffList: MutableList<Staff>,
    private val onStaffSelected: (Staff) -> Unit,
    private val onStaffDeleted: (Int) -> Unit
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {

    inner class StaffViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtStaffName: TextView = itemView.findViewById(R.id.txtStaffName)
        val txtStaffPhone: TextView = itemView.findViewById(R.id.txtStaffPhone)
        val txtStaffEmail: TextView = itemView.findViewById(R.id.txtStaffEmail)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDeleteStaff)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_staff, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val staff = staffList[position]
        holder.txtStaffName.text = staff.ten
        holder.txtStaffPhone.text = "SĐT: ${staff.sdt}"
        holder.txtStaffEmail.text = "Email: ${staff.email}"

        holder.itemView.setOnClickListener {
            onStaffSelected(staff)
        }

        holder.btnDelete.setOnClickListener {
            onStaffDeleted(staff.id)
        }
    }

    override fun getItemCount(): Int = staffList.size
}
