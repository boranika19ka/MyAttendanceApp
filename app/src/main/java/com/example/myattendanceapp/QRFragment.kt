package com.example.myattendanceapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myattendanceapp.api.RetrofitClient
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QRFragment : Fragment() {

    private lateinit var tvCurrentTime: TextView
    private lateinit var currentView: View
    private var scanType = "check_in" // track which button was clicked

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateTime()
            handler.postDelayed(this, 1000)
        }
    }

    // QR Scanner launcher
    private val scanLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            // QR scanned! → call API
            handleCheckIn(currentView, scanType)
        } else {
            Toast.makeText(requireContext(), "Scan cancelled!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r, container, false)
        currentView = view

        tvCurrentTime = view.findViewById(R.id.tvCurrentTime)

        // Set date
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        view.findViewById<TextView>(R.id.tvCurrentDate).text = dateFormat.format(Date())

        // Start clock
        handler.post(updateTimeRunnable)

        // Check In Button → open camera
        view.findViewById<Button>(R.id.btnCheckIn).setOnClickListener {
            scanType = "check_in"
            openCamera()
        }

        // Check Out Button → open camera
        view.findViewById<Button>(R.id.btnCheckOut).setOnClickListener {
            scanType = "check_out"
            openCamera()
        }

        // Back Button
        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, HomeFragment())
                .commit()
        }

        // Load today's attendance
        loadTodayAttendance(view)

        return view
    }

    private fun openCamera() {
        val options = ScanOptions()
        options.setPrompt("Scan QR Code")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        scanLauncher.launch(options)
    }

    private fun updateTime() {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        tvCurrentTime.text = timeFormat.format(Date())
    }

    private fun loadTodayAttendance(view: View) {
        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.today("Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()!!
                    val attendance = body.attendance

                    if (attendance != null) {
                        view.findViewById<TextView>(R.id.tvCheckInTime).text =
                            attendance.check_in_time?.substring(0, 5) ?: "--:--"
                        view.findViewById<TextView>(R.id.tvCheckOutTime).text =
                            attendance.check_out_time?.substring(0, 5) ?: "--:--"
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    private fun handleCheckIn(view: View, type: String) {
        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.checkIn("Bearer $token")

                if (response.isSuccessful) {
                    val body = response.body()!!

                    when (body.type) {
                        "check_in" -> {
                            val status = if (body.status == "late") "⚠️ យឺត!" else "✅ ទាន់ម៉ោង!"
                            Toast.makeText(requireContext(),
                                "ចូលធ្វើការ ${body.time?.substring(0,5)} - $status",
                                Toast.LENGTH_LONG).show()
                        }
                        "check_out" -> {
                            Toast.makeText(requireContext(),
                                "ចេញធ្វើការ ${body.time?.substring(0,5)} ✅",
                                Toast.LENGTH_LONG).show()
                        }
                        "done" -> {
                            Toast.makeText(requireContext(),
                                "អ្នកបានកត់ត្រារួចហើយថ្ងៃនេះ!",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Reload today attendance
                    loadTodayAttendance(view)

                } else {
                    Toast.makeText(requireContext(), "Failed! Try again.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection error!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimeRunnable)
    }
}