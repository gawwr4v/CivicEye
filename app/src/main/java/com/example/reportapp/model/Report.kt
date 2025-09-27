package com.example.reportapp.model

import java.io.Serializable

data class Report(
    val timestamp: Long,          // keep this locally
    val photoPath: String,
    val issueType: String,        // maps to backend "category"
    val issueDescription: String, // maps to backend "text"
    val latitude: Double,
    val longitude: Double,
    var status: String = "Under Verification" // default status
) : Serializable
