package com.example.myattendanceapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotifyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notify, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerNotify)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Get user name
        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "Staff") ?: "Staff"

        // Sample notifications
        val notifications = listOf(
            NotifyItem(
                "✅",
                "វត្តមានបានកត់ត្រា",
                "អ្នកបានចូលធ្វើការថ្ងៃនេះដោយជោគជ័យ!",
                "ថ្ងៃនេះ"
            ),
            NotifyItem(
                "📝",
                "សំណើសុំច្បាប់",
                "សំណើសុំច្បាប់របស់អ្នកកំពុងរង់ចាំការអនុម័ត",
                "ម្សិលមិញ"
            ),
            NotifyItem(
                "⏰",
                "រំលឹកវត្តមាន",
                "សូមកុំភ្លេចកត់ត្រាវត្តមានថ្ងៃនេះ!",
                "2 ថ្ងៃមុន"
            ),
            NotifyItem(
                "💰",
                "ប្រាក់ខែ",
                "ប្រាក់ខែខែនេះនឹងបើកនៅថ្ងៃទី 30",
                "1 សប្តាហ៍មុន"
            ),
            NotifyItem(
                "🎉",
                "សូមស្វាគមន៍!",
                "សូមស្វាគមន៍មក $name! សូមប្រើប្រាស់កម្មវិធីនេះ!",
                "1 ខែមុន"
            )
        )

        if (notifications.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.adapter = NotifyAdapter(notifications)
        }

        return view
    }
}