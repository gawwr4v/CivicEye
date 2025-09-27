package com.example.reportapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.reportapp.databinding.ActivityReportDetailBinding
import com.example.reportapp.model.Report
import java.text.SimpleDateFormat
import java.util.*

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val report = intent.getSerializableExtra("report") as? Report
        if (report == null) {
            finish() // nothing to show
            return
        }

        // Load ic_mage
        Glide.with(this)
            .load(report.photoPath)
            .centerCrop()
            .into(binding.detailPhoto)

        // Set description
        binding.detailDescription.text = report.issueDescription

        // Coordinates
        binding.detailCoords.text = "Lat: ${report.latitude}, Lng: ${report.longitude}"

        // Format timestamp into human-readable local time
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedDate = sdf.format(Date(report.timestamp))
        binding.detailTime.text = "Reported on: $formattedDate"

        // Issue type
        binding.detailIssueType.text = "Issue Type: ${report.issueType}"
    }
}
