package com.example.halidao.admin

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.halidao.ManageFoodFragment
import com.example.halidao.ManageStaffFragment
import com.example.halidao.ManageStatisticsFragment

class ManagePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // ✅ Đảm bảo chỉ có 3 tab

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ManageFoodFragment()  // ✅ Món Ăn
            1 -> ManageStaffFragment() // ✅ Nhân Viên
            2 -> ManageStatisticsFragment()  // ✅ Thống Kê
            else -> throw IllegalStateException("Vị trí tab không hợp lệ: $position") // Bắt lỗi nếu có tab dư
        }
    }
}
