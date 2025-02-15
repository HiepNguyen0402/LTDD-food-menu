package com.example.halidao

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EnterTableNumberActivity : AppCompatActivity() {

    private lateinit var edtTableNumber: EditText
    private lateinit var btnConfirmTable: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_table_number)
        databaseHelper = DatabaseHelper(this)

        edtTableNumber = findViewById(R.id.edt_table_number)
        btnConfirmTable = findViewById(R.id.btn_confirm_table)

        btnConfirmTable.setOnClickListener {
            val tableNumber = edtTableNumber.text.toString().trim()

            if (tableNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số bàn!", Toast.LENGTH_SHORT).show()
            } else {
                val db = databaseHelper.readableDatabase
                val query =
                    """
                        SELECT tt.ten FROM BanAn b
                        JOIN TrangThai tt ON b.id_trang_thai = tt.id
                        WHERE b.so_ban = ?
                    """
                val cursor = db.rawQuery(query, arrayOf(tableNumber))

                if (cursor.moveToFirst()) {
                    val trangThai = cursor.getString(0)
                    if (trangThai == "Bàn trống") {
                        val intent = Intent(this, FoodActivity::class.java)
                        intent.putExtra("TABLE_NUMBER", tableNumber)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Bàn đã có người sử dụng!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Bàn không tồn tại!", Toast.LENGTH_SHORT).show()
                }

                cursor.close()
                db.close()
            }
        }
    }
}