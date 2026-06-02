package com.example.myattendanceapp

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendanceapp.api.LeaveRequest
import com.example.myattendanceapp.api.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

class LeaveFragment : Fragment() {

    private var startDate = ""
    private var endDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leave, container, false)

        val etReason = view.findViewById<EditText>(R.id.etReason)
        val btnStartDate = view.findViewById<Button>(R.id.btnStartDate)
        val btnEndDate = view.findViewById<Button>(R.id.btnEndDate)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerLeaves)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Start Date picker
        btnStartDate.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                btnStartDate.text = date
            }
        }

        // End Date picker
        btnEndDate.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                btnEndDate.text = date
            }
        }

        // Submit leave request
        btnSubmit.setOnClickListener {
            val reason = etReason.text.toString().trim()

            if (reason.isEmpty()) {
                etReason.error = "សូមបញ្ចូលមូលហេតុ!"
                return@setOnClickListener
            }
            if (startDate.isEmpty()) {
                Toast.makeText(requireContext(), "សូមជ្រើសរើសថ្ងៃចាប់ផ្តើម!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (endDate.isEmpty()) {
                Toast.makeText(requireContext(), "សូមជ្រើសរើសថ្ងៃបញ្ចប់!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val token = prefs.getString("token", "") ?: ""

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.submitLeave(
                        "Bearer $token",
                        LeaveRequest(reason, startDate, endDate)
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(),
                            "ដាក់សំណើដោយជោគជ័យ! ✅",
                            Toast.LENGTH_LONG).show()

                        // Clear form
                        etReason.setText("")
                        startDate = ""
                        endDate = ""
                        btnStartDate.text = "ជ្រើសរើសថ្ងៃ"
                        btnEndDate.text = "ជ្រើសរើសថ្ងៃ"

                        // Reload list
                        loadLeaves(recycler, tvEmpty, token)

                    } else {
                        Toast.makeText(requireContext(), "មានបញ្ហា! សូមព្យាយាមម្តងទៀត!", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Connection error!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Load my leaves
        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        loadLeaves(recycler, tvEmpty, token)

        return view
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val date = String.format("%d-%02d-%02d", year, month + 1, day)
                onDateSelected(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadLeaves(recycler: RecyclerView, tvEmpty: TextView, token: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.myLeaves("Bearer $token")
                if (response.isSuccessful) {
                    val list = response.body()!!.leaves
                    if (list.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        recycler.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        recycler.visibility = View.VISIBLE
                        recycler.adapter = LeaveAdapter(list)
                    }
                }
            } catch (e: Exception) {
                tvEmpty.visibility = View.VISIBLE
            }
        }
    }
}