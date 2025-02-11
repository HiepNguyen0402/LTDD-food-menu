package com.example.halidao

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EnterTableNumberActivity : AppCompatActivity() {

    private lateinit var edtTableNumber: EditText
    private lateinit var btnConfirmTable: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_table_number)

        edtTableNumber = findViewById(R.id.edt_table_number)
        btnConfirmTable = findViewById(R.id.btn_confirm_table)

        btnConfirmTable.setOnClickListener {
            val tableNumber = edtTableNumber.text.toString().trim()

            if (tableNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số bàn!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, FoodActivity::class.java)
                intent.putExtra("TABLE_NUMBER", tableNumber) // Gửi số bàn đến màn hình menu món
                startActivity(intent)
            }
        }
    }
}