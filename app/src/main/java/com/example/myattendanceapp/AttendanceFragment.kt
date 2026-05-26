package com.example.myattendanceapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendanceapp.api.RetrofitClient
import kotlinx.coroutines.launch

class AttendanceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerAttendance)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Load attendance
        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.myAttendance("Bearer $token")

                if (response.isSuccessful) {
                    val list = response.body()!!.attendances

                    if (list.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        recycler.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        recycler.visibility = View.VISIBLE
                        recycler.adapter = AttendanceAdapter(list)
                    }
                }

            } catch (e: Exception) {
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = "Connection error!"
            }
        }

        return view
    }
}