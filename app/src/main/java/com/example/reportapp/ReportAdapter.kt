package com.example.reportapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reportapp.databinding.ItemReportBinding
import com.example.reportapp.model.Report
import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter(
    private val reports: MutableList<Report>,
    private val onClick: (Report) -> Unit,
    private val onLongClick: (Report, Int) -> Unit,
    private val onEmpty: () -> Unit   // Callback for empty state
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report) {
            // Null-safe text assignments
            binding.tvIssueType.text = report.issueType ?: "Unknown"
            binding.tvDescription.text = report.issueDescription ?: "No description"
            binding.tvCoords.text = "Lat: ${report.latitude ?: 0.0}, Lng: ${report.longitude ?: 0.0}"

            // Format timestamp safely
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            binding.tvTimestamp.text = report.timestamp?.let { sdf.format(Date(it)) } ?: "Unknown date"

            // Status tag
            val status = report.status ?: "Unknown"
            binding.tvStatus.text = status
            val color = when (status) {
                "Under Verification" -> android.R.color.holo_orange_dark
                "Ongoing" -> android.R.color.holo_blue_dark
                "Resolved" -> android.R.color.holo_green_dark
                else -> android.R.color.darker_gray
            }
            binding.tvStatus.setBackgroundResource(R.drawable.bg_status_tag)
            binding.tvStatus.setBackgroundColor(ContextCompat.getColor(binding.root.context, color))

            // Load thumbnail safely
            val photo = report.photoPath
            if (!photo.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(photo)
                    .centerCrop()
                    .into(binding.ivThumbnail)
            } else {
                binding.ivThumbnail.setImageResource(R.drawable.ic_image_placeholder)
            }

            // Click listeners
            binding.root.setOnClickListener { onClick(report) }
            binding.root.setOnLongClickListener {
                onLongClick(report, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    override fun getItemCount(): Int = reports.size

    fun removeAt(position: Int) {
        reports.removeAt(position)
        notifyItemRemoved(position)

        // Notify activity if list is empty
        if (reports.isEmpty()) {
            onEmpty()
        }
    }
}
