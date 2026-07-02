package com.example.myattendanceapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendanceapp.api.OvertimeItem

class OvertimeAdapter(private val list: List<OvertimeItem>) :
    RecyclerView.Adapter<OvertimeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvHours: TextView = view.findViewById(R.id.tvHours)
        val tvReason: TextView = view.findViewById(R.id.tvReason)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_overtime, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvDate.text = item.date
        holder.tvHours.text = "⏰ ${item.hours} ម៉ោង"
        holder.tvReason.text = "មូលហេតុ: ${item.reason}"

        when (item.status) {
            "pending" -> {
                holder.tvStatus.text = "Pending"
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"))
            }
            "approved" -> {
                holder.tvStatus.text = "Approved"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            }
            "rejected" -> {
                holder.tvStatus.text = "Rejected"
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"))
            }
        }
    }

    override fun getItemCount() = list.size
}