package com.example.reportapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reportapp.ReportAdapter
import com.example.reportapp.ReportDetailActivity
import com.example.reportapp.ReportStorage
import com.example.reportapp.databinding.FragmentTrackReportsBinding
import com.example.reportapp.model.Report
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class TrackReportFragment : Fragment() {

    private var _binding: FragmentTrackReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReportAdapter
    private val reportList = mutableListOf<Report>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reportRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reportList.addAll(ReportStorage.loadReports(requireContext()))

        adapter = ReportAdapter(
            reportList,
            onClick = { report ->
                val intent = Intent(requireContext(), ReportDetailActivity::class.java)
                intent.putExtra("report", report as Serializable)
                startActivity(intent)
            },
            onLongClick = { report, position ->
                val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                val formattedDate = sdf.format(Date(report.timestamp))

                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete Report")
                    setMessage(
                        "Do you want to delete this report?\n\n" +
                                "Issue: ${report.issueType}\n" +
                                "Description: ${report.issueDescription}\n" +
                                "Reported on: $formattedDate"
                    )
                    setPositiveButton("Yes") { _, _ ->
                        adapter.removeAt(position)
                        ReportStorage.saveReports(requireContext(), reportList)
                    }
                    setNegativeButton("No", null)
                    show()
                }
            },
            onEmpty = { toggleEmptyState() }
        )

        binding.reportRecyclerView.adapter = adapter

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        toggleEmptyState()
    }

    private fun toggleEmptyState() {
        if (reportList.isEmpty()) {
            binding.reportRecyclerView.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.reportRecyclerView.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
