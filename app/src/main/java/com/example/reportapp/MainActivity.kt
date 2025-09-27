package com.example.reportapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.reportapp.databinding.ActivityMainBinding
import com.example.reportapp.fragments.HomeFragment
import com.example.reportapp.fragments.ReportFragment
import com.example.reportapp.fragments.TrackReportFragment
import com.example.reportapp.fragments.ProfileFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load HomeFragment first
        if (savedInstanceState == null) {
            openFragment(HomeFragment())
        }

        // Handle nav bar item clicks
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    openFragment(HomeFragment())
                    true
                }
                R.id.nav_report -> {
                    openFragment(ReportFragment())
                    true
                }
                R.id.nav_my_reports -> {
                    openFragment(TrackReportFragment())
                    true
                }
                R.id.nav_profile -> {
                    openFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
