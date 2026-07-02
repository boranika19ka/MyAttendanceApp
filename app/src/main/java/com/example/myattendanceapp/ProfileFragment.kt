package com.example.myattendanceapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myattendanceapp.api.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Save image URI
            val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            prefs.edit().putString("profile_image", it.toString()).apply()

            // Show image
            Glide.with(this).load(it).circleCrop().into(imgProfile)
            Toast.makeText(requireContext(), "រូបភាពបានផ្លាស់ប្តូរ!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "Staff") ?: "Staff"
        val email = prefs.getString("email", "") ?: ""
        val role = prefs.getString("role", "staff") ?: "staff"
        val salary = prefs.getString("salary", "0$") ?: "0$"
        val profileImage = prefs.getString("profile_image", null)

        imgProfile = view.findViewById(R.id.imgProfile)

        // Load profile image
        if (profileImage != null) {
            Glide.with(this).load(Uri.parse(profileImage)).circleCrop().into(imgProfile)
        }

        // Greeting based on time
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "🌅 Good Morning"
            hour < 17 -> "☀️ Good Afternoon"
            else -> "🌙 Good Evening"
        }
        view.findViewById<TextView>(R.id.tvGreeting).text = "$greeting, $name!"

        // Set info
        view.findViewById<TextView>(R.id.tvProfileName).text = name
        view.findViewById<TextView>(R.id.tvProfileRole).text = role
        view.findViewById<TextView>(R.id.tvName).text = name
        view.findViewById<TextView>(R.id.tvEmail).text = email
        view.findViewById<TextView>(R.id.tvRole).text = role
        view.findViewById<TextView>(R.id.tvSalary).text = salary

        // Change photo button
        view.findViewById<LinearLayout>(R.id.btnChangePhoto).setOnClickListener {
            pickImage.launch("image/*")
        }

        // Dark mode toggle
        val switchDarkMode = view.findViewById<Switch>(R.id.switchDarkMode)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = isDarkMode

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Change Password
        view.findViewById<LinearLayout>(R.id.btnChangePassword).setOnClickListener {
            showChangePasswordDialog()
        }

        // Notification settings
        view.findViewById<LinearLayout>(R.id.btnNotification).setOnClickListener {
            Toast.makeText(requireContext(), "ការជូនដំណឹងបានបើក! 🔔", Toast.LENGTH_SHORT).show()
        }

        // About app
        view.findViewById<LinearLayout>(R.id.btnAbout).setOnClickListener {
            showAboutDialog()
        }

        // Logout
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            prefs.edit().clear().apply()
            (activity as MainActivity).hideBottomNav()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, LoginFragment())
                .commit()
        }

        return view
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_change_password, null)

        AlertDialog.Builder(requireContext())
            .setTitle("ផ្លាស់ប្តូរពាក្យសម្ងាត់")
            .setView(dialogView)
            .setPositiveButton("រក្សាទុក") { _, _ ->
                val oldPass = dialogView.findViewById<EditText>(R.id.etOldPassword).text.toString()
                val newPass = dialogView.findViewById<EditText>(R.id.etNewPassword).text.toString()
                val confirmPass = dialogView.findViewById<EditText>(R.id.etConfirmPassword).text.toString()

                if (newPass != confirmPass) {
                    Toast.makeText(requireContext(), "ពាក្យសម្ងាត់មិនត្រូវគ្នា!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                changePassword(oldPass, newPass)
            }
            .setNegativeButton("បោះបង់", null)
            .show()
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.changePassword(
                    "Bearer $token",
                    com.example.myattendanceapp.api.ChangePasswordRequest(oldPassword, newPassword)
                )
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "ពាក្យសម្ងាត់បានផ្លាស់ប្តូរ! ✅", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "ពាក្យសម្ងាត់ចាស់មិនត្រឹមត្រូវ!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Connection error!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("អំពីកម្មវិធី")
            .setMessage(
                "MyAttendance App\n\n" +
                        "កំណែ: 1.0.0\n" +
                        "បង្កើតដោយ: Long Boranika\n" +
                        "គោលបំណង: ប្រព័ន្ធគ្រប់គ្រងវត្តមានបុគ្គលិក\n\n" +
                        "© 2026 All Rights Reserved"
            )
            .setPositiveButton("យល់ព្រម", null)
            .show()
    }
}