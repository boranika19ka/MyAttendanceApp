package com.example.myattendanceapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.myattendanceapp.api.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var bannerHandler: Handler? = null
    private var bannerRunnable: Runnable? = null

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

        setupBanner(view)
        return view
    }

    private fun setupBanner(view: View) {
        val banners = listOf(
            BannerItem("🎉", "សូមស្វាគមន៍!", "សូមស្វាគមន៍មកកាន់ MyAttendance App", "#1A237E"),
            BannerItem("📅", "កុំភ្លេចកត់ត្រា!", "សូមកត់ត្រាវត្តមានរៀងរាល់ថ្ងៃ", "#1565C0"),
            BannerItem("💰", "ប្រាក់ខែ!", "ប្រាក់ខែខែនេះនឹងបើកនៅថ្ងៃទី 30", "#0D47A1"),
            BannerItem("🏆", "សូមអរគុណ!", "អរគុណសម្រាប់ការខិតខំប្រឹងប្រែង!", "#283593")
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.bannerViewPager)
        val dotsLayout = view.findViewById<android.widget.LinearLayout>(R.id.dotsLayout)

        viewPager.adapter = BannerAdapter(banners)

        bannerHandler = Handler(Looper.getMainLooper())
        bannerRunnable = object : Runnable {
            override fun run() {
                if (!isAdded) return
                val next = (viewPager.currentItem + 1) % banners.size
                viewPager.setCurrentItem(next, true)
                bannerHandler?.postDelayed(this, 3000)
            }
        }
        bannerHandler?.postDelayed(bannerRunnable!!, 3000)

        updateDots(dotsLayout, 0, banners.size)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (!isAdded) return
                updateDots(dotsLayout, position, banners.size)
            }
        })
    }

    private fun updateDots(dotsLayout: android.widget.LinearLayout, current: Int, total: Int) {
        if (!isAdded) return
        dotsLayout.removeAllViews()
        for (i in 0 until total) {
            val dot = TextView(requireContext())
            dot.text = if (i == current) "●" else "○"
            dot.textSize = 12f
            dot.setTextColor(
                if (i == current) android.graphics.Color.parseColor("#1A237E")
                else android.graphics.Color.parseColor("#AAAAAA")
            )
            dot.setPadding(4, 0, 4, 0)
            dotsLayout.addView(dot)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerRunnable?.let { bannerHandler?.removeCallbacks(it) }
        bannerHandler = null
    }
}