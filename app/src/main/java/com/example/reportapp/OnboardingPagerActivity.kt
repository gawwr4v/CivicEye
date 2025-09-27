package com.example.reportapp

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.reportapp.adapter.OnboardingAdapter
import com.example.reportapp.databinding.ActivityOnboardingPagerBinding
import com.example.reportapp.model.OnboardingItem

class OnboardingPagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingPagerBinding
    private lateinit var onboardingItems: List<OnboardingItem>
    private lateinit var adapter: OnboardingAdapter
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        prefs = getSharedPreferences("onboarding", MODE_PRIVATE)
        if (prefs.getBoolean("isOnboardingDone", false)) {
            navigateToLogin()
            return
        }

        binding = ActivityOnboardingPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingItems()
        setupViewPager()
        setupButtons()
    }

    private fun setupOnboardingItems() {
        onboardingItems = listOf(
            OnboardingItem(
                R.drawable.onboarding2,
                "Spot a problem?",
                "Snap a photo, share the location, and submit a report in seconds."
            ),
            OnboardingItem(
                R.drawable.onboarding3,
                "Track every step",
                "Get updates when your issue is reviewed, fixed, or responded to."
            ),
            OnboardingItem(
                R.drawable.onboarding4,
                "Make a difference together",
                "Be the person to make the change and help your neighborhood."
            )
        )
    }

    private fun setupViewPager() {
        adapter = OnboardingAdapter(onboardingItems)
        binding.viewPagerOnboarding.adapter = adapter

        // Initial dots state
        updateDots(0)

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Update dots
                updateDots(position)

                // Back arrow should always be visible here
                binding.ivBack.visibility = View.VISIBLE

                // Next button text
                binding.btnNext.text = if (position == onboardingItems.size - 1) "Finish" else "Next"
            }
        })
    }

    private fun setupButtons() {
        // Back arrow click
        binding.ivBack.setOnClickListener {
            val current = binding.viewPagerOnboarding.currentItem
            if (current > 0) {
                binding.viewPagerOnboarding.currentItem = current - 1
            } else {
                // If on first pager page (Onboarding2), go back to Onboarding1Activity
                startActivity(Intent(this, Onboarding1Activity::class.java))
                finish()
            }
        }

        // Skip button click
        binding.tvSkip.setOnClickListener {
            completeOnboarding()
        }

        // Next button click
        binding.btnNext.setOnClickListener {
            val nextIndex = binding.viewPagerOnboarding.currentItem + 1
            if (nextIndex < onboardingItems.size) {
                binding.viewPagerOnboarding.currentItem = nextIndex
            } else {
                completeOnboarding()
            }
        }
    }

    private fun updateDots(position: Int) {
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3)
        for (i in dots.indices) {
            dots[i].setBackgroundResource(
                if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }

    private fun completeOnboarding() {
        prefs.edit().putBoolean("isOnboardingDone", true).apply()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // ✅ Hardware back button acts same as back arrow
    override fun onBackPressed() {
        val current = binding.viewPagerOnboarding.currentItem
        if (current > 0) {
            binding.viewPagerOnboarding.currentItem = current - 1
        } else {
            startActivity(Intent(this, Onboarding1Activity::class.java))
            finish()
        }
    }


}
