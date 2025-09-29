package com.example.reportapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val checkmarkImage = findViewById<ImageView>(R.id.checkmarkImage)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnNewReport = findViewById<Button>(R.id.btnNewReport)

        // Fade-in animation for checkmark
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000 // 1 second
            fillAfter = true // stay visible after animation
        }

        checkmarkImage.visibility = View.VISIBLE // ensure it's visible
        checkmarkImage.startAnimation(fadeIn)


        // Go to Home tab inside MainActivity
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigateTo", "home") // tell MainActivity to open HomeFragment
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // File another report (open ReportFragment again)
        btnNewReport.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigateTo", "report") // tell MainActivity to open ReportFragment
            startActivity(intent)
            finish()
        }
    }
}
