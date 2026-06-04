package com.example.myattendanceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class BannerItem(
    val icon: String,
    val title: String,
    val message: String,
    val bgColor: String
)

class BannerAdapter(private val list: List<BannerItem>) :
    RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvBannerIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvBannerTitle)
        val tvMessage: TextView = view.findViewById(R.id.tvBannerMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvIcon.text = item.icon
        holder.tvTitle.text = item.title
        holder.tvMessage.text = item.message
        holder.itemView.setBackgroundColor(
            android.graphics.Color.parseColor(item.bgColor)
        )
    }

    override fun getItemCount() = list.size
}