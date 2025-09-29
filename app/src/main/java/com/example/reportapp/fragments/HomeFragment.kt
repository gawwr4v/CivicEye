package com.example.reportapp.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.reportapp.R
import com.example.reportapp.adapters.ArticlesAdapter
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

        // ✅ Open ProfileFragment
        binding.btnProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // ✅ Quick Access - File a Report
        binding.btnFileReport.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ReportFragment())
                .addToBackStack(null)
                .commit()
        }

        // ✅ Quick Access - Track Reports
        binding.btnTrackReports.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, TrackReportFragment())
                .addToBackStack(null)
                .commit()
        }

        // ✅ Setup articles list
        val articles = listOf(
            "How to use Civic Eye",
            "5 ways to be a responsible citizen",
            "Report issues effectively",
            "Community guidelines"
        )

        // 🔹 LayoutManager ensures vertical scrolling
        binding.articlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 🔹 Adapter with click listener
        binding.articlesRecyclerView.adapter = ArticlesAdapter(articles) { selectedArticle ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ArticleDetailFragment.newInstance(selectedArticle))
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
