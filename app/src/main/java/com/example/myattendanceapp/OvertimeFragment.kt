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
import com.example.myattendanceapp.api.OvertimeRequest
import com.example.myattendanceapp.api.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

class OvertimeFragment : Fragment() {

    private var selectedDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_overtime, container, false)

        val etHours = view.findViewById<EditText>(R.id.etHours)
        val etReason = view.findViewById<EditText>(R.id.etReason)
        val btnDate = view.findViewById<Button>(R.id.btnDate)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerOvertimes)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Date picker
        btnDate.setOnClickListener {
            showDatePicker { date ->
                selectedDate = date
                btnDate.text = date
            }
        }

        // Submit
        btnSubmit.setOnClickListener {
            val hours = etHours.text.toString().trim()
            val reason = etReason.text.toString().trim()

            if (selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "សូមជ្រើសរើសថ្ងៃ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (hours.isEmpty()) {
                etHours.error = "សូមបញ្ចូលម៉ោង!"
                return@setOnClickListener
            }
            if (reason.isEmpty()) {
                etReason.error = "សូមបញ្ចូលមូលហេតុ!"
                return@setOnClickListener
            }

            val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            val token = prefs.getString("token", "") ?: ""

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.submitOvertime(
                        "Bearer $token",
                        OvertimeRequest(selectedDate, hours.toDouble(), reason)
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(),
                            "ដាក់សំណើដោយជោគជ័យ! ✅",
                            Toast.LENGTH_LONG).show()

                        // Clear form
                        etHours.setText("")
                        etReason.setText("")
                        selectedDate = ""
                        btnDate.text = "ជ្រើសរើសថ្ងៃ"

                        // Reload list
                        loadOvertimes(recycler, tvEmpty, token)
                    } else {
                        Toast.makeText(requireContext(), "មានបញ្ហា!", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Connection error!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Load overtime list
        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        loadOvertimes(recycler, tvEmpty, token)

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

    private fun loadOvertimes(recycler: RecyclerView, tvEmpty: TextView, token: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.myOvertimes("Bearer $token")
                if (response.isSuccessful) {
                    val list = response.body()!!.overtimes
                    if (list.isEmpty()) {
                        tvEmpty.visibility = View.VISIBLE
                        recycler.visibility = View.GONE
                    } else {
                        tvEmpty.visibility = View.GONE
                        recycler.visibility = View.VISIBLE
                        recycler.adapter = OvertimeAdapter(list)
                    }
                }
            } catch (e: Exception) {
                tvEmpty.visibility = View.VISIBLE
            }
        }
    }
}