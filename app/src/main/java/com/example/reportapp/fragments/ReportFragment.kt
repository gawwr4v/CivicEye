package com.example.reportapp.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.reportapp.ReportStorage
import com.example.reportapp.RetrofitClient
import com.example.reportapp.UploadResponse
import com.example.reportapp.databinding.FragmentReportBinding
import com.example.reportapp.model.Report
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var photoFile: File? = null
    private var photoUri: Uri? = null

    private var isPhotoTaken = false
    private var locationAdded = false
    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    companion object {
        private const val PERMISSION_REQUEST_CAMERA = 201
        private const val PERMISSION_REQUEST_LOCATION = 202
        private const val REQUEST_IMAGE_CAPTURE = 102
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.submitReportBtn.isEnabled = false

        setupIssueTypeSpinner()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.takePhotoBtn.setOnClickListener { checkCameraPermission() }
        binding.addLocationBtn.setOnClickListener { checkLocationPermission() }
        binding.submitReportBtn.setOnClickListener { submitReport() }

        binding.descriptionInput.addTextChangedListener { updateSubmitState() }
        binding.issueTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateSubmitState()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupIssueTypeSpinner() {
        val issueTypes = listOf("Select issue type", "Pothole", "Street Light", "Garbage", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, issueTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.issueTypeSpinner.adapter = adapter
    }

    private fun checkCameraPermission() {
        val permissions = mutableListOf<String>()
        if (!isPermissionGranted(Manifest.permission.CAMERA)) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            !isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_CAMERA)
        } else {
            openCamera()
        }
    }

    private fun checkLocationPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_LOCATION)
        } else {
            fetchCoordinates()
        }
    }

    private fun isPermissionGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openCamera()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    showSettingsDialog("Camera permission is required to take photos.")
                } else {
                    Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCoordinates()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showSettingsDialog("Location permission is required to fetch coordinates.")
                } else {
                    Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSettingsDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("$message\n\nPlease enable it in Settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
            return
        }
        photoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", photoFile!!)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", requireContext().cacheDir)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(photoUri).centerCrop().into(binding.imageView)
            isPhotoTaken = true
            updateSubmitState()
        }
    }

    private fun fetchCoordinates() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLatitude = location.latitude
                lastLongitude = location.longitude
                binding.coordinatesText.text = "Lat: $lastLatitude, Lng: $lastLongitude"
                locationAdded = true
            } else {
                Toast.makeText(requireContext(), "Could not get location", Toast.LENGTH_SHORT).show()
                locationAdded = false
            }
            updateSubmitState()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Could not get location", Toast.LENGTH_SHORT).show()
            locationAdded = false
            updateSubmitState()
        }
    }

    private fun updateSubmitState() {
        val description = binding.descriptionInput.text.toString().trim()
        val issueTypeSelected = binding.issueTypeSpinner.selectedItem.toString() != "Select issue type"
        binding.submitReportBtn.isEnabled =
            isPhotoTaken && locationAdded && issueTypeSelected && description.length >= 10
    }

    private fun submitReport() {
        val description = binding.descriptionInput.text.toString().trim()
        if (!isPhotoTaken || !locationAdded || description.length < 10) {
            Toast.makeText(requireContext(), "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newReport = Report(
            timestamp = System.currentTimeMillis(),
            photoPath = photoFile?.absolutePath ?: "",
            issueType = binding.issueTypeSpinner.selectedItem.toString(),
            issueDescription = description,
            latitude = lastLatitude ?: 0.0,
            longitude = lastLongitude ?: 0.0
        )

        uploadReportToServer(newReport)

        val reports = ReportStorage.loadReports(requireContext())
        reports.add(0, newReport)
        ReportStorage.saveReports(requireContext(), reports)

        Toast.makeText(requireContext(), "Report submitted ✅", Toast.LENGTH_SHORT).show()
    }

    private fun uploadReportToServer(report: Report) {
        val file = File(report.photoPath)
        if (!file.exists()) {
            Toast.makeText(requireContext(), "❌ File not found: ${report.photoPath}", Toast.LENGTH_LONG).show()
            return
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val textBody = report.issueDescription.toRequestBody("text/plain".toMediaTypeOrNull())
        val latBody = report.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lngBody = report.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val catBody = report.issueType.toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitClient.apiService.uploadReport(body, textBody, latBody, lngBody, catBody)
            .enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show()
                        file.delete()
                    } else {
                        val errorMsg = response.errorBody()?.string()
                        Toast.makeText(requireContext(), "Server error ${response.code()}: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Upload failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
