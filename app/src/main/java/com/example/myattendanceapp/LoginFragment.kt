package com.example.myattendanceapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myattendanceapp.api.LoginRequest
import com.example.myattendanceapp.api.RetrofitClient
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val cardEmail = view.findViewById<CardView>(R.id.cardEmail)
        val cardPassword = view.findViewById<CardView>(R.id.cardPassword)

        etEmail.setOnFocusChangeListener { _, _ ->
            cardEmail.setCardBackgroundColor(Color.WHITE)
        }
        etPassword.setOnFocusChangeListener { _, _ ->
            cardPassword.setCardBackgroundColor(Color.WHITE)
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            cardEmail.setCardBackgroundColor(Color.WHITE)
            cardPassword.setCardBackgroundColor(Color.WHITE)

            if (email.isEmpty()) {
                cardEmail.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                etEmail.error = "Please enter your email"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                cardEmail.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                etEmail.error = "Please enter a valid email"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                cardPassword.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                etPassword.error = "Please enter your password"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                cardPassword.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                etPassword.error = "Password must be at least 6 characters"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.login(
                        LoginRequest(email, password)
                    )

                    if (response.isSuccessful) {
                        val body = response.body()!!

                        val prefs = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                        prefs.edit()
                            .putString("token", body.token)
                            .putString("name", body.user.name)
                            .putString("email", body.user.email)
                            .putString("role", body.user.role)
                            .putInt("user_id", body.user.id)
                            .apply()

                        Toast.makeText(requireContext(), "Welcome ${body.user.name}!", Toast.LENGTH_SHORT).show()

                        if (body.user.role == "admin") {
                            Toast.makeText(requireContext(), "Admin Login!", Toast.LENGTH_SHORT).show()
                        } else {
                            (activity as MainActivity).showBottomNav()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, HomeFragment())
                                .commit()
                        }

                    } else {
                        cardEmail.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                        cardPassword.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                        Toast.makeText(requireContext(), "Invalid email or password!", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Connection error! Check server.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}