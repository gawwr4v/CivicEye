package com.example.reportapp

import android.content.Context
import com.example.reportapp.model.Report
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ReportStorage {

    private const val PREFS_NAME = "Reports"
    private const val KEY_REPORT_LIST = "report_list_json"
    private val gson = Gson()

    fun saveReports(context: Context, reports: List<Report>) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(reports)
        sharedPref.edit().putString(KEY_REPORT_LIST, json).apply()
    }

    fun loadReports(context: Context): MutableList<Report> {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPref.getString(KEY_REPORT_LIST, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Report>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
