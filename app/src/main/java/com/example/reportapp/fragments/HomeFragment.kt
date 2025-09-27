package com.example.reportapp.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.reportapp.R
import com.example.reportapp.databinding.FragmentHomeBinding
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileFile: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileFile = File(requireContext().filesDir, "profile_picture.jpg")
        loadProfile()

        // ✅ Open ProfileFragment directly
        binding.btnProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // ✅ Open ReportFragment
        binding.btnFileReport.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ReportFragment())
                .addToBackStack(null)
                .commit()
        }

        // ✅ Open TrackReportFragment
        binding.btnTrackReport.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, TrackReportFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        val sharedPref = requireContext().getSharedPreferences("UserProfile", Activity.MODE_PRIVATE)
        val username = sharedPref.getString("username", "User")
        binding.greetingText.text = "Hello, $username"
        loadProfileImage(binding.btnProfile)
    }

    private fun loadProfileImage(imageView: ShapeableImageView) {
        if (profileFile.exists()) {
            Glide.with(this)
                .load(profileFile)
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_profile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
