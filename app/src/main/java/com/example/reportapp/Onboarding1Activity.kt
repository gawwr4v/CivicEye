package com.example.reportapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reportapp.databinding.ActivityOnboarding1Binding

class Onboarding1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboarding1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboarding1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Go to ViewPager onboarding screens
        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this, OnboardingPagerActivity::class.java))
            finish()
        }
    }
}
