package com.example.activity_tracker_sdk.core

import android.content.Context

data class ActivityTrackerInitOptions(
    val serverEndpoint: String,
    val appId: Int,
    val applicationContext: Context,
    val eventBatchLimit: Int = 20, // default limit
    val sendDelayInMilliSeconds: Long = 500 // delay in milliseconds, default 0.5 seconds
)