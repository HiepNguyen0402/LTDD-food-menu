package com.example.halidao.nhanvien

import DatabaseHelper
import OrderDetail
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.R
import com.google.android.material.tabs.TabLayout
import com.example.halidao.datmon.FoodActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.squareup.picasso.Picasso
import java.nio.charset.StandardCharsets
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAction: Button
    private lateinit var btnAddFood: Button
    private lateinit var dbHelper: DatabaseHelper
    private var orderId: Int = -1
    private lateinit var adapter: OrderDetailAdapter
    private var idBan: Int = -1
    private var selectedOrderDetailId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        dbHelper = DatabaseHelper(this)
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerViewOrderDetail)
        btnAction = findViewById(R.id.btnAction)
        btnAddFood = findViewById(R.id.btnAddFood)

        // ✅ Đảm bảo lấy đúng idBan từ Intent
        idBan = intent.getIntExtra("ID_BAN", -1)
        if (idBan == -1) {
            Toast.makeText(this, "Lỗi: Không xác định được bàn!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ✅ Chỉ lấy orderId một lần
        val latestOrderId = dbHelper.getLatestUnpaidOrder(idBan)
        if (latestOrderId != orderId) {
            Log.d("OrderDetailActivity", "🔄 Cập nhật orderId từ $orderId thành $latestOrderId")
            orderId = latestOrderId
        }

        Log.d("OrderDetailActivity", "🎯 Bàn: $idBan, Order ID: $orderId")

        recyclerView.layoutManager = LinearLayoutManager(this)

        tabLayout.addTab(tabLayout.newTab().setText("Chưa làm"))
        tabLayout.addTab(tabLayout.newTab().setText("Đang làm"))
        tabLayout.addTab(tabLayout.newTab().setText("Đã xong"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                loadOrderDetails(position)
                btnAddFood.visibility = if (position == 0) View.VISIBLE else View.GONE
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        if (orderId != -1) {
            loadOrderDetails(0)
        }

        btnAction.setOnClickListener {
            when (tabLayout.selectedTabPosition) {
                0, 1 -> {
                    selectedOrderDetailId?.let { orderDetailId ->
                        val currentStatus = if (tabLayout.selectedTabPosition == 0) 4 else 5
                        val newStatus = if (currentStatus == 4) 5 else 6
                        updateOrderStatus(currentStatus, newStatus, orderDetailId)
                    } ?: Toast.makeText(this, "Hãy chọn một món trước!", Toast.LENGTH_SHORT).show()
                }
                2 -> processPayment()
            }
        }

        btnAddFood.setOnClickListener {
            Log.d("OrderDetailActivity", "Bấm nút thêm món, sử dụng orderId: $orderId, bàn: $idBan")

            if (orderId == -1) {
                Toast.makeText(this, "Không tìm thấy đơn hàng hợp lệ!", Toast.LENGTH_SHORT).show()

            }

            val intent = Intent(this, FoodActivity::class.java)
            intent.putExtra("TABLE_NUMBER", idBan.toString())  // ✅ Đảm bảo bàn được truyền đúng
            intent.putExtra("ORDER_ID", orderId) // ✅ Đảm bảo truyền đúng orderId
            startActivity(intent)
        }
    }
    private fun loadOrderDetails(status: Int) {
        val statusId = when (status) {
            0 -> 4
            1 -> 5
            2 -> 6
            else -> 4
        }

        Log.d("OrderDetailActivity", "🔎 Đang tải món ăn cho orderId: $orderId, bàn: $idBan, trạng thái: $statusId")

        val items: List<OrderDetail> = dbHelper.getOrderDetailsByStatus(orderId, statusId)

        Log.d("OrderDetailActivity", "📌 Số lượng món lấy được: ${items.size}")

        adapter = OrderDetailAdapter(items) { orderDetailId ->
            selectedOrderDetailId = orderDetailId
            btnAction.visibility = if (statusId == 4 || statusId == 5) View.VISIBLE else View.GONE
        }
        recyclerView.adapter = adapter
        // ✅ Kiểm tra xem tất cả món có phải đều là "Đã xong" không
        val allCompleted = dbHelper.areAllItemsCompleted(orderId)

// ✅ Hiển thị nút "Thanh toán" nếu tất cả món đã hoàn thành
        btnAction.visibility = if (statusId == 6 && allCompleted) View.VISIBLE else View.GONE


        // Cập nhật nội dung nút dựa theo trạng thái
        when (status) {
            0 -> btnAction.text = "Chuyển sang Đang làm"
            1 -> btnAction.text = "Chuyển sang Đã xong"
            2 -> btnAction.text = "Thanh toán"
        }
    }


    private fun updateOrderStatus(currentStatus: Int, newStatus: Int, orderDetailId: Int) {
        val success = dbHelper.updateOrderStatus(orderId, currentStatus, newStatus, orderDetailId)
        if (success) {
            loadOrderDetails(tabLayout.selectedTabPosition)
        } else {
            Toast.makeText(this, "Không thể cập nhật trạng thái!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processPayment() {
        if (idBan == -1) {
            Toast.makeText(this, "Lỗi: Không xác định được bàn!", Toast.LENGTH_SHORT).show()
            return
        }
        val totalAmount = dbHelper.getTotalAmount(orderId) // 🔥 Gọi hàm vừa tạo để lấy tổng tiền
        val paymentMethods = arrayOf("Tiền mặt", "Chuyển khoản")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Chọn phương thức thanh toán")
            .setItems(paymentMethods) { _, which ->
                val selectedMethod = paymentMethods[which]
                if (selectedMethod == "Chuyển khoản") {
                    showQRCodeDialog(orderId, totalAmount)
                } else {
                    confirmPayment(selectedMethod)
                }
            }
            .show()
    }
    private fun showQRCodeDialog(orderId: Int, amount: Int) {
        val qrCodeData = generateQRCodeData(orderId, amount)
        val imageView = ImageView(this)
        Picasso.get().load(qrCodeData).into(imageView)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Quét mã QR để thanh toán")
            .setView(imageView)
            .setPositiveButton("Đã chuyển khoản") { _, _ ->
                confirmPayment("Chuyển khoản") // ✅ Khách tự xác nhận thanh toán
            }
            .setNegativeButton("Hủy", null)
            .show()
    }


    private fun generateQRCodeData(orderId: Int, amount: Int): String {
        val transferMessage = "Thanh toan don hang #$orderId".replace(" ", "%20")
        val bankCode = "ACB" // ✅ Mã ngân hàng ACB
        val accountNumber = "12264277" // ✅ Số tài khoản ACB của bạn

        // ✅ Sử dụng API VietQR chính thức để tạo mã QR hợp lệ
        return "https://img.vietqr.io/image/$bankCode-$accountNumber-qr_only.png?amount=$amount&addInfo=$transferMessage"
    }



    private fun generateQRCodeBitmap(data: String): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 500, 500)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bmp
    }


    private fun confirmPayment(paymentMethod: String) {
        val totalAmount = dbHelper.getTotalAmount(orderId) // 🔥 Gọi hàm để lấy tổng tiền
        val updated = dbHelper.payOrder(orderId, totalAmount, paymentMethod)

        if (!updated) {
            Toast.makeText(this, "Lỗi: Không thể cập nhật trạng thái thanh toán!", Toast.LENGTH_SHORT).show()
            return
        }

        dbHelper.updateTableStatus(idBan, 1) // Đặt bàn về trạng thái "Trống"
        val timestamp = System.currentTimeMillis() / 1000
        val newOrderId = dbHelper.insertOrder(idBan, timestamp, 0, 2)

        if (newOrderId != -1L) {
            orderId = newOrderId.toInt()
            loadOrderDetails(0)
        }

        val intent = Intent(this, OrderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }


}
