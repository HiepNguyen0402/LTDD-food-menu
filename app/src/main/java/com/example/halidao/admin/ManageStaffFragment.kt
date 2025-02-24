package com.example.halidao

import DatabaseHelper
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
    private lateinit var spinnerStaffRole: Spinner
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
        spinnerStaffRole = view.findViewById(R.id.spinnerStaffRole)

// Tạo Adapter cho Spinner
        val roles = resources.getStringArray(R.array.staff_roles)
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        spinnerStaffRole.adapter = adapterSpinner

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
        val roleIndex = spinnerStaffRole.selectedItemPosition + 1 // ID role bắt đầu từ 1

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStaffId == null) {
            databaseHelper.insertStaff(Staff(0, name, phone, email, "123456", roleIndex))
            Toast.makeText(requireContext(), "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show()
        } else {
            databaseHelper.updateStaff(selectedStaffId!!, name, phone, email, roleIndex)
            Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            selectedStaffId = null
            btnSaveStaff.text = "Thêm nhân viên"
        }
        clearInputs()
        loadStaffList()
    }
    private fun onStaffSelected(staff: Staff) {
        selectedStaffId = staff.id
        edtStaffName.setText(staff.ten)
        edtStaffPhone.setText(staff.sdt)
        edtStaffEmail.setText(staff.email)

        // ✅ Chọn đúng role trong Spinner
        spinnerStaffRole.setSelection(staff.idRole - 1)

        btnSaveStaff.text = "Cập nhật nhân viên"
    }
    private fun clearInputs() {
        edtStaffName.text.clear()
        edtStaffPhone.text.clear()
        edtStaffEmail.text.clear()
        spinnerStaffRole.setSelection(0) // ✅ Chọn về "Quản lý" mặc định
    }
    private fun onStaffDeleted(staffId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa nhân viên")
            .setMessage("Bạn có chắc muốn xóa nhân viên này?")
            .setPositiveButton("Xóa") { _, _ ->
                databaseHelper.deleteStaff(staffId)
                clearInputs()
                loadStaffList() // Cập nhật lại danh sách nhân viên sau khi xóa
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}