package com.example.myattendanceapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Start with Login screen
        replaceFragment(LoginFragment())

        // Hide bottom nav on login
        bottomNav.visibility = android.view.View.GONE

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_notify -> replaceFragment(HomeFragment()) // later
                R.id.nav_profile -> replaceFragment(HomeFragment()) // later
            }
            true
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    fun showBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = android.view.View.VISIBLE
    }

    fun hideBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = android.view.View.GONE
    }
}