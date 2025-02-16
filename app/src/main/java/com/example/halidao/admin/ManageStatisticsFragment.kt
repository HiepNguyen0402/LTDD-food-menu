package com.example.halidao

import DatabaseHelper
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        if (statisticsList.isEmpty()) {
            txtTotalRevenue.text = "Chưa có dữ liệu doanh thu"
            barChart.clear()
            pieChart.clear()
            lineChart.clear()
            barChart.setNoDataText("Không có dữ liệu")
            pieChart.setNoDataText("Không có dữ liệu")
            lineChart.setNoDataText("Không có dữ liệu")
        } else {
            txtTotalRevenue.text = "Tổng doanh thu: ${databaseHelper.getTotalRevenue()} VND"
            loadBarChart(statisticsList)  // Thêm dòng này
            loadPieChart()                // Thêm dòng này
            loadLineChart()               // Thêm dòng này
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
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Thống kê doanh thu")

            // Thêm tiêu đề
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Tháng")
            headerRow.createCell(1).setCellValue("Doanh thu (VND)")

            // Lấy dữ liệu thống kê từ database
            val statisticsList = databaseHelper.getRevenueStatistics()
            for ((index, data) in statisticsList.withIndex()) {
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(data.month)
                row.createCell(1).setCellValue(data.revenue.toDouble())
            }

            // Lưu file Excel
            val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ThongKe")
            if (!folder.exists()) folder.mkdirs()
            val file = File(folder, "ThongKe_DoanhThu.xlsx")

            val fileOutputStream = FileOutputStream(file)
            workbook.write(fileOutputStream)
            fileOutputStream.close()
            workbook.close()

            Toast.makeText(requireContext(), "Đã xuất file Excel: ${file.absolutePath}", Toast.LENGTH_LONG).show()

            // Gửi file qua Gmail
            sendEmailWithAttachment(file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Lỗi khi xuất file Excel", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmailWithAttachment(file: File) {
        val uri = Uri.fromFile(file)

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.ms-excel"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("email@example.com")) // Thay email người nhận
            putExtra(Intent.EXTRA_SUBJECT, "Báo cáo doanh thu")
            putExtra(Intent.EXTRA_TEXT, "Gửi bạn báo cáo doanh thu theo tháng.")
            putExtra(Intent.EXTRA_STREAM, uri)
        }

        startActivity(Intent.createChooser(emailIntent, "Gửi email..."))
    }

}
