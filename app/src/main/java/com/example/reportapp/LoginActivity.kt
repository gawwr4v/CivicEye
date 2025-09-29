package com.example.reportapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.reportapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button -> close login
        binding.backButton.setOnClickListener { finish() }

        // Toggle between Login & Signup
        binding.toggleLogin.setOnClickListener { switchToLogin() }
        binding.toggleSignup.setOnClickListener { switchToSignup() }

        // Default to Login mode
        switchToLogin()

        // Handle Sign In / Sign Up
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (isLoginMode) {
                // ✅ Let user login with anything
                goToMain()
            } else {
                // ✅ Signup mode
                if (password == confirmPassword && email.isNotEmpty()) {
                    goToMain()
                } else {
                    binding.etConfirmPassword.error = "Passwords do not match"
                }
            }
        }
    }

    private fun switchToLogin() {
        isLoginMode = true
        binding.toggleLogin.setBackgroundResource(R.drawable.toggle_selected)
        binding.toggleLogin.setTextColor(getColor(android.R.color.white))
        binding.toggleSignup.setBackgroundResource(android.R.color.transparent)
        binding.toggleSignup.setTextColor(getColor(R.color.green_primary))

        binding.layoutConfirmPassword.visibility = View.GONE
        binding.btnSignIn.text = "Sign In"
    }

    private fun switchToSignup() {
        isLoginMode = false
        binding.toggleSignup.setBackgroundResource(R.drawable.toggle_selected)
        binding.toggleSignup.setTextColor(getColor(android.R.color.white))
        binding.toggleLogin.setBackgroundResource(android.R.color.transparent)
        binding.toggleLogin.setTextColor(getColor(R.color.green_primary))

        binding.layoutConfirmPassword.visibility = View.VISIBLE
        binding.btnSignIn.text = "Create Account"
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
