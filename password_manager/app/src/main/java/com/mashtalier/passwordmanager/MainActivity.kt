package com.mashtalier.passwordmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.mashtalier.passwordmanager.fragments.Home
import com.mashtalier.passwordmanager.fragments.Add
import com.mashtalier.passwordmanager.fragments.Profile
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mashtalier.passwordmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(Home())
        }

        configureBottomNavigation()
    }

    private fun configureBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(Home())
                    true
                }
                R.id.nav_add -> {
                    loadFragment(Add())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(Profile())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
