package com.example.reportapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reportapp.databinding.ActivityConfirmationBinding
import com.example.reportapp.fragments.ReportFragment

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fade-in checkmark
        binding.checkmarkImage.apply {
            alpha = 0f
            animate().alpha(1f).setDuration(1000).start()
        }

        // Go back to Home (clear back stack)
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Start new report
        binding.btnNewReport.setOnClickListener {
            startActivity(Intent(this, ReportFragment::class.java))
            finish()
        }
    }
}
