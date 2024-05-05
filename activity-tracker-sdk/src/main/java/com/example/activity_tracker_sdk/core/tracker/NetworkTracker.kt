package com.example.activity_tracker_sdk.core.tracker

import android.util.Log
import com.example.activity_tracker_sdk.core.GlobalVariables
import com.example.activity_tracker_sdk.data.models.Event
import com.example.activity_tracker_sdk.data.models.RequestData
import com.example.activity_tracker_sdk.data.network.ApiServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Responsible for send event to network.
 */
internal class NetworkTracker : BaseTracker {
    private val eventCache = mutableListOf<Event>()
    private var isSending = false
    private val sendCoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun trackEvent(event: Event) {
        synchronized(eventCache) {
            eventCache.add(event)
        }
        if (!isSending) {
            sendEventsWithDelay()
        }
    }

    override fun close() {
        // Accept some events can be missed. Because this is action from users.
        eventCache.clear()
    }

    private fun sendEventsWithDelay() {
        sendCoroutineScope.launch {
            isSending = true
            while (eventCache.isNotEmpty()) {
                sendNextBatch()
                delay(GlobalVariables.getSendDelayInMilliSeconds())
            }
            isSending = false
        }
    }

    private suspend fun sendNextBatch() {
        val eventsToSend = synchronized(eventCache) {
            val events = eventCache.take(GlobalVariables.getEventBatchLimit())
            eventCache.removeAll(events)
            events
        }
        if (eventsToSend.isNotEmpty()) {
            repeatSendingEvents(eventsToSend, maxRetries)
        }
    }

    private suspend fun repeatSendingEvents(events: List<Event>, retries: Int) {
        var attempt = 0
        var success = false

        while (attempt < retries && !success) {
            Log.d("NetworkTracker", "Start send events size=${events.size}, attemptCount=$attempt")
            val response = sendEvents(events)
            success = response?.code() == 200
            if (!success) {
                attempt++
                Log.e("NetworkTracker", "Attempt $attempt failed, retrying...")
                delay(GlobalVariables.getSendDelayInMilliSeconds())
            } else {
                Log.d("NetworkTracker", "Send successful")
            }
        }
        if (!success) {
            Log.e("NetworkTracker", "All attempts to send events failed")
        }
    }

    private suspend fun sendEvents(events: List<Event>): Response<ResponseBody>? {
        val appId = GlobalVariables.getAppId()
            ?: throw IllegalStateException("App ID is not set. Call initialize() first.")
        val userId = GlobalVariables.getUserId()
            ?: throw IllegalStateException("User ID is not set. Call initialize() first.")
        val serverEndpoint = GlobalVariables.getServerEndpoint()
            ?: throw IllegalStateException("Server endpoint is not set. Call initialize() first.")

        val requestData =
            RequestData(appId = appId, userId = userId, events = events.toTypedArray())
        return try {
            ApiServiceManager.eventApiService?.sendData(serverEndpoint, requestData)
        } catch (ex: Throwable) {
            Log.e("NetworkTracker", "Network unavailable")
            null
        }
    }

    companion object {
        private const val maxRetries = 3
    }
}