package com.example.halidao

import DatabaseHelper
import GmailSender
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.adapter.StatisticsAdapter
import com.example.halidao.data.model.Statistics
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import com.example.halidao.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageStatisticsFragment : Fragment() {

    private lateinit var txtTotalRevenue: TextView
    private lateinit var txtTotalOrders: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnExportExcel: Button
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var lineChart: LineChart
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: StatisticsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manage_statistics, container, false)

        txtTotalRevenue = view.findViewById(R.id.txtTotalRevenue)
        txtTotalOrders = view.findViewById(R.id.txtTotalOrders)
        recyclerView = view.findViewById(R.id.recyclerViewStatistics)
        btnExportExcel = view.findViewById(R.id.btnExportExcel)
        barChart = view.findViewById(R.id.barChartStatistics)
        pieChart = view.findViewById(R.id.pieChartStatistics)
        lineChart = view.findViewById(R.id.lineChartStatistics)

        databaseHelper = DatabaseHelper(requireContext())
        loadStatistics()

        btnExportExcel.setOnClickListener {
            exportToExcel()
        }


        return view
    }


    private fun loadStatistics() {
        val statisticsList = databaseHelper.getRevenueStatistics()
        val totalOrders = databaseHelper.getTotalOrders() // Gọi hàm để lấy tổng số đơn hàng

        if (statisticsList.isEmpty()) {
            txtTotalRevenue.text = "Chưa có dữ liệu doanh thu"
            txtTotalOrders.text = "Chưa có đơn hàng nào" // Hiển thị nếu không có đơn hàng
            barChart.clear()
            pieChart.clear()
            lineChart.clear()
            barChart.setNoDataText("Không có dữ liệu")
            pieChart.setNoDataText("Không có dữ liệu")
            lineChart.setNoDataText("Không có dữ liệu")
        } else {
            txtTotalRevenue.text = "Tổng doanh thu: ${databaseHelper.getTotalRevenue()} VND"
            txtTotalOrders.text = "Tổng số đơn hàng: $totalOrders" // Cập nhật UI hiển thị tổng số đơn hàng
            loadBarChart(statisticsList)
            loadPieChart()
            loadLineChart()
        }

        adapter = StatisticsAdapter(requireContext(), statisticsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }



    private fun loadBarChart(statisticsList: List<Statistics>) {
        val entries = statisticsList.mapIndexed { index, data ->
            BarEntry(index.toFloat(), data.revenue.toFloat())
        }

        val dataSet = BarDataSet(entries, "Doanh thu theo tháng").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
        }

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.invalidate()
    }

    private fun loadPieChart() {
        val pieEntries = mutableListOf<PieEntry>()
        val revenueByCategory = databaseHelper.getRevenueByCategory()

        revenueByCategory.forEach { (category, revenue) ->
            pieEntries.add(PieEntry(revenue.toFloat(), category))
        }

        val dataSet = PieDataSet(pieEntries, "Doanh thu theo danh mục").apply {
            colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
            valueTextColor = Color.BLACK
        }

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.invalidate()
    }

    private fun loadLineChart() {
        val entries = databaseHelper.getDailyRevenue().mapIndexed { index, data ->
            Entry(index.toFloat(), data.second.toFloat())
        }

        val dataSet = LineDataSet(entries, "Xu hướng doanh thu theo ngày").apply {
            color = Color.MAGENTA
            valueTextColor = Color.BLACK
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.description.isEnabled = false
        lineChart.invalidate()
    }
    private fun exportToExcel() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("email", null)

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy email của bạn. Hãy đăng nhập lại!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val workbook = XSSFWorkbook()

            // 📌 **1️⃣ Sheet 1: Tổng quan doanh thu**
            val sheet1 = workbook.createSheet("Thống kê chi tiết")
            val headerRow1 = sheet1.createRow(0)
            headerRow1.createCell(0).setCellValue("Ngày")
            headerRow1.createCell(1).setCellValue("Tổng đơn hàng")
            headerRow1.createCell(2).setCellValue("Doanh thu (VND)")

            val revenueList = databaseHelper.getDailyRevenue()
            for ((index, data) in revenueList.withIndex()) {
                val row = sheet1.createRow(index + 1)
                row.createCell(0).setCellValue(data.first) // Ngày
                row.createCell(1).setCellValue(databaseHelper.getTotalOrders().toDouble()) // Tổng đơn hàng
                row.createCell(2).setCellValue(data.second.toDouble()) // Doanh thu
            }




            // 📌 **3️⃣ Sheet 3: Món ăn bán chạy nhất**
            val sheet3 = workbook.createSheet("Món ăn bán chạy")
            val headerRow3 = sheet3.createRow(0)
            headerRow3.createCell(0).setCellValue("Tên món")
            headerRow3.createCell(1).setCellValue("Số lần đặt")
            headerRow3.createCell(2).setCellValue("Tổng doanh thu (VND)")

            val bestSellingFoods = databaseHelper.getBestSellingFoods()
            for ((index, food) in bestSellingFoods.withIndex()) {
                val row = sheet3.createRow(index + 1)
                row.createCell(0).setCellValue(food.tenMon)
                row.createCell(1).setCellValue(food.soLuong.toDouble()) // Số lần đặt
                row.createCell(2).setCellValue(food.tongTien.toDouble()) // Doanh thu từ món đó
            }

           

            // 📌 **5️⃣ Sheet 5: Tổng số lượng bán theo từng món**
            val sheet5 = workbook.createSheet("Tổng số lượng bán")
            val headerRow5 = sheet5.createRow(0)
            headerRow5.createCell(0).setCellValue("Tên món")
            headerRow5.createCell(1).setCellValue("Tổng số lượng bán")

            val totalFoodSales = databaseHelper.getTotalFoodSales()
            for ((index, food) in totalFoodSales.withIndex()) {
                val row = sheet5.createRow(index + 1)
                row.createCell(0).setCellValue(food.tenMon)
                row.createCell(1).setCellValue(food.soLuong.toDouble()) // Tổng số lượng đã bán
            }
            // 📌 **1️⃣ Thống kê doanh thu theo ngày trong tuần**



            // 📌 **2️⃣ Hiệu suất sử dụng bàn**
            val sheet7 = workbook.createSheet("Hiệu suất sử dụng bàn")
            val headerRow7 = sheet7.createRow(0)
            headerRow7.createCell(0).setCellValue("Mã bàn")
            headerRow7.createCell(1).setCellValue("Số lần sử dụng")

            val tableUsage = databaseHelper.getTableUsageStats()
            for ((index, data) in tableUsage.withIndex()) {
                val row = sheet7.createRow(index + 1)
                row.createCell(0).setCellValue(data.first.toDouble()) // Mã bàn
                row.createCell(1).setCellValue(data.second.toDouble()) // Số lần sử dụng
            }

            // 📌 **Lưu file**
            val folder = File(requireContext().getExternalFilesDir(null), "ThongKe")
            if (!folder.exists()) folder.mkdirs()
            val file = File(folder, "ThongKe_ChiTiet.xlsx")

            FileOutputStream(file).use { fos ->
                workbook.write(fos)
            }
            workbook.close()

            Toast.makeText(requireContext(), "Đã xuất file Excel: ${file.absolutePath}", Toast.LENGTH_LONG).show()

            // 📌 **Gửi file qua Gmail**
            sendEmailWithAttachment(userEmail, file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Lỗi khi xuất file Excel", Toast.LENGTH_SHORT).show()
        }
    }

    fun getOrderStatusText(status: Int): String {
        return when (status) {
            1 -> "Đang chờ xử lý"
            2 -> "Đang giao hàng"
            3 -> "Đã hoàn thành"
            4 -> "Đã hủy"
            else -> "Không xác định"
        }
    }


    private fun sendEmailWithAttachment(email: String, file: File) {
        CoroutineScope(Dispatchers.IO).launch { // Chạy trên background thread
            try {
                val sender = GmailSender("meocandam@gmail.com", "fjhhuqvyiewtcwlh")
                sender.sendMail(
                    "Báo cáo doanh thu",
                    "Gửi bạn báo cáo doanh thu theo tháng.",
                    email,
                    file.absolutePath
                )
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Email đã được gửi!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Gửi email thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
