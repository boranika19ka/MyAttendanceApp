package com.example.myattendanceapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendanceapp.api.AttendanceItem

class AttendanceAdapter(private val list: List<AttendanceItem>) :
    RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvCheckIn: TextView = view.findViewById(R.id.tvCheckIn)
        val tvCheckOut: TextView = view.findViewById(R.id.tvCheckOut)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        // Date
        holder.tvDate.text = item.date

        // Check In time
        holder.tvCheckIn.text = item.check_in_time?.substring(0, 5) ?: "--:--"

        // Check Out time
        holder.tvCheckOut.text = item.check_out_time?.substring(0, 5) ?: "--:--"

        // Status
        when (item.status) {
            "present" -> {
                holder.tvStatus.text = "✅ ទាន់ម៉ោង"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            }
            "late" -> {
                holder.tvStatus.text = "⚠️ យឺត"
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"))
            }
            "absent" -> {
                holder.tvStatus.text = "❌ អវត្តមាន"
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"))
            }
        }
    }

    override fun getItemCount() = list.size
}