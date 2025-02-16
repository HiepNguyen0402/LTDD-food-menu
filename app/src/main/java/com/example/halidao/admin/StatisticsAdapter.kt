package com.example.halidao.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.example.halidao.data.model.Statistics

class StatisticsAdapter(
    private val context: Context,
    private val statisticsList: List<Statistics>
) : RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {

    class StatisticsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMonth: TextView = view.findViewById(R.id.txtMonth)
        val txtRevenue: TextView = view.findViewById(R.id.txtRevenue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_statistics, parent, false)
        return StatisticsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val item = statisticsList[position]
        holder.txtMonth.text = "Tháng: ${item.month}"
        holder.txtRevenue.text = "Doanh thu: ${item.revenue} VND"
    }

    override fun getItemCount(): Int = statisticsList.size
}
