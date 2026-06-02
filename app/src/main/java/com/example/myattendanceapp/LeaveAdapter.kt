package com.example.myattendanceapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myattendanceapp.api.LeaveItem

class LeaveAdapter(private val list: List<LeaveItem>) :
    RecyclerView.Adapter<LeaveAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDates: TextView = view.findViewById(R.id.tvDates)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvReason: TextView = view.findViewById(R.id.tvReason)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leave, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvDates.text = "${item.start_date} → ${item.end_date}"
        holder.tvReason.text = "មូលហេតុ: ${item.reason}"

        when (item.status) {
            "pending" -> {
                holder.tvStatus.text = "⏳ រង់ចាំ"
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"))
            }
            "approved" -> {
                holder.tvStatus.text = "✅ អនុម័ត"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            }
            "rejected" -> {
                holder.tvStatus.text = "❌ បដិសេធ"
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"))
            }
        }
    }

    override fun getItemCount() = list.size
}