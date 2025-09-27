package com.example.reportapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reportapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button behavior - close activity
        binding.backButton.setOnClickListener {
            finish()
        }

        // Temporary: Skip login -> go to HomeActivity
        binding.tvSkipLogin.setOnClickListener {
            // Optional: toast so you know it's the temp bypass
            Toast.makeText(this, "Skipping login (temp)", Toast.LENGTH_SHORT).show()

            // Start HomeActivity (or whichever activity you want to land on)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()  // Finish login so user can't go back here with back button
        }

        // Sign in button placeholder (no auth)
        binding.btnSignIn.setOnClickListener {
            // If you want to also bypass login via sign-in button during development:
            // val intent = Intent(this, HomeActivity::class.java)
            // startActivity(intent); finish()

            // Or show a small message
            Toast.makeText(this, "Sign in not implemented (temp)", Toast.LENGTH_SHORT).show()
        }
    }
}
