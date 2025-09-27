package com.example.reportapp.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.reportapp.R
import com.example.reportapp.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileFile: File
    private var tempPhotoFile: File? = null

    companion object {
        private const val REQUEST_CAMERA = 102
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileFile = File(requireContext().filesDir, "profile_picture.jpg")

        // ✅ Limit username length
        binding.editUsername.filters = arrayOf(InputFilter.LengthFilter(20))

        // ✅ Load saved profile
        val prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val savedUsername = prefs.getString("username", "")
        binding.editUsername.setText(savedUsername)

        if (profileFile.exists()) {
            loadImageIntoView(profileFile)
        } else {
            binding.profileImage.setImageResource(R.drawable.ic_profile)
        }

        // ✅ Save button
        binding.btnSave.setOnClickListener {
            val username = binding.editUsername.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit().putString("username", username).apply()
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }

        // ✅ Profile picture click
        binding.profileImage.setOnClickListener { showImagePickerDialog() }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Choose from Gallery", "Take Photo with Camera")
        AlertDialog.Builder(requireContext())
            .setTitle("Update Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery()
                    1 -> checkCameraPermissionAndOpenCamera()
                }
            }
            .show()
    }

    private fun pickImageFromGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            takePhotoWithCamera()
        }
    }

    private fun takePhotoWithCamera() {
        tempPhotoFile = File.createTempFile("temp_profile_", ".jpg", requireContext().cacheDir)
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", tempPhotoFile!!)
        cameraLauncher.launch(uri)
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                saveCompressedImage(it)
                loadImageIntoView(profileFile)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && tempPhotoFile != null) {
                saveCompressedImage(Uri.fromFile(tempPhotoFile))
                loadImageIntoView(profileFile)
            }
        }

    private fun saveCompressedImage(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)

            val fos = FileOutputStream(profileFile, false)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 50, fos)
            fos.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadImageIntoView(file: File) {
        Glide.with(this)
            .load(file)
            .circleCrop()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.profileImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
