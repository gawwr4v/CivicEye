package com.example.reportapp

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("uploadReport")   // ✅ backend route from routes.py
    fun uploadReport(
        @Part image: MultipartBody.Part,      // required (user can’t submit without photo)
        @Part("text") text: RequestBody,      // description
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("category") category: RequestBody   // issue type
    ): Call<UploadResponse>
}
