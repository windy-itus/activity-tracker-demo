package com.example.activity_tracker_sdk.data.network

import android.content.Context
import com.example.activity_tracker_sdk.core.GlobalVariables

internal object ApiServiceManager {
    internal var eventApiService: EventApiService? = null
        private set
        get() {
            if (field == null) {
                val baseUrl = GlobalVariables.getServerEndpoint()
                    ?: throw IllegalStateException("User ID is not set. Call initialize() first.")
                val context: Context? = GlobalVariables.getApplicationContext()
                if (context != null) {
                    field = ServiceBuilder()
                        .setBaseUrl(baseUrl)
                        .createService(EventApiService::class.java)
                }
            }

            return field
        }
}