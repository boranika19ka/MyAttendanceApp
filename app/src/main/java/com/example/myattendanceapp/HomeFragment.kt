package com.example.myattendanceapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myattendanceapp.api.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Show bottom nav
        (activity as MainActivity).showBottomNav()

        // Get saved user info
        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "Staff") ?: "Staff"
        val token = prefs.getString("token", "") ?: ""

        // Set name
        view.findViewById<TextView>(R.id.tvName).text = "Welcome, $name"

        // Set today's date
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        view.findViewById<TextView>(R.id.tvDate).text = dateFormat.format(Date())

        // Load real stats from API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.myStats("Bearer $token")
                if (response.isSuccessful) {
                    val stats = response.body()!!
                    view.findViewById<TextView>(R.id.tvPresent).text = stats.present.toString()
                    view.findViewById<TextView>(R.id.tvAbsent).text = stats.absent.toString()
                    view.findViewById<TextView>(R.id.tvLeave).text = stats.leave.toString()
                    view.findViewById<TextView>(R.id.tvDayOff).text = stats.late.toString()
                }
            } catch (e: Exception) {
                // keep 0
            }
        }

        // Menu buttons
        view.findViewById<LinearLayout>(R.id.btnQR).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, QRFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.btnAttendance).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, AttendanceFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<LinearLayout>(R.id.btnLeave).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, LeaveFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<LinearLayout>(R.id.btnOvertime).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, OvertimeFragment())
                .addToBackStack(null)
                .commit()
        }
        return view
    }
}