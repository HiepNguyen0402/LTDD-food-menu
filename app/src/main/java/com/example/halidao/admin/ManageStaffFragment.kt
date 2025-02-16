package com.example.halidao

import DatabaseHelper
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.halidao.adapter.StaffAdapter
import com.example.halidao.data.model.Staff

class ManageStaffFragment : Fragment() {

    private lateinit var edtStaffName: EditText
    private lateinit var edtStaffPhone: EditText
    private lateinit var edtStaffEmail: EditText
    private lateinit var edtStaffRole: EditText
    private lateinit var btnSaveStaff: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: StaffAdapter
    private var selectedStaffId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manage_staff, container, false)

        edtStaffName = view.findViewById(R.id.edtStaffName)
        edtStaffPhone = view.findViewById(R.id.edtStaffPhone)
        edtStaffEmail = view.findViewById(R.id.edtStaffEmail)
        edtStaffRole = view.findViewById(R.id.edtStaffRole)
        btnSaveStaff = view.findViewById(R.id.btnSaveStaff)
        recyclerView = view.findViewById(R.id.recyclerViewStaff)

        databaseHelper = DatabaseHelper(requireContext())
        loadStaffList()

        btnSaveStaff.setOnClickListener {
            saveStaff()
        }

        return view
    }

    private fun loadStaffList() {
        val staffList = databaseHelper.getAllStaff().toMutableList()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StaffAdapter(requireContext(), staffList,
            onStaffSelected = { staff -> onStaffSelected(staff) },
            onStaffDeleted = { staffId -> onStaffDeleted(staffId) }
        )
        recyclerView.adapter = adapter
    }

    private fun saveStaff() {
        val name = edtStaffName.text.toString()
        val phone = edtStaffPhone.text.toString()
        val email = edtStaffEmail.text.toString()
        val role = edtStaffRole.text.toString()

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || role.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStaffId == null) {
            databaseHelper.insertStaff(Staff(0, name, phone, email, "123456", role.toInt()))
            Toast.makeText(requireContext(), "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show()
        } else {
            databaseHelper.updateStaff(selectedStaffId!!, name, phone, email, role.toInt())
            Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            selectedStaffId = null
            btnSaveStaff.text = "Thêm nhân viên"
        }

        loadStaffList()
    }

    private fun onStaffSelected(staff: Staff) {
        selectedStaffId = staff.id
        edtStaffName.setText(staff.ten)
        edtStaffPhone.setText(staff.sdt)
        edtStaffEmail.setText(staff.email)
        edtStaffRole.setText(staff.idRole.toString())
        btnSaveStaff.text = "Cập nhật nhân viên"
    }
    private fun onStaffDeleted(staffId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa nhân viên")
            .setMessage("Bạn có chắc muốn xóa nhân viên này?")
            .setPositiveButton("Xóa") { _, _ ->
                databaseHelper.deleteStaff(staffId)
                loadStaffList() // Cập nhật lại danh sách nhân viên sau khi xóa
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}