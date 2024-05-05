package com.example.activity_tracker_sdk.data.network

import com.example.activity_tracker_sdk.data.models.RequestData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

internal interface EventApiService {
    @PUT
    suspend fun sendData(@Url url: String, @Body requestData: RequestData): Response<ResponseBody>
}