package com.example.halidao.admin

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.halidao.ManageFoodFragment
import com.example.halidao.ManageStaffFragment
import com.example.halidao.ManageStatisticsFragment

class ManagePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4 // 4 tab (Món Ăn, Nhân Viên, Đơn Hàng, Bàn Ăn)

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ManageFoodFragment()
            1 -> ManageStaffFragment()
            2 -> ManageStatisticsFragment()
            else -> ManageFoodFragment()
        }
    }
}
