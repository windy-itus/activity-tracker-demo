package com.example.activitytrackerdemo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.activity_tracker_sdk.core.ActivityTrackerInitOptions
import com.example.activity_tracker_sdk.core.ActivityTrackerManager
import com.example.activity_tracker_sdk.utils.toJsonString

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnInitialize = findViewById<Button>(R.id.btnInitializeSDK)
        btnInitialize.setOnClickListener {
            // Call SDK initialize function
            ActivityTrackerManager.initialize(
                ActivityTrackerInitOptions(
                    serverEndpoint = "{{SERVER_ENDPOINT}}",
                    appId = {{APP_ID}},
                    applicationContext = applicationContext
                )
            )
        }

        val btnShutdown = findViewById<Button>(R.id.btnShutdownSDK)
        btnShutdown.setOnClickListener {
            ActivityTrackerManager.shutdown()
        }

        val btnSendStartLevelEvent = findViewById<Button>(R.id.btnSendStartLevelEvent)
        btnSendStartLevelEvent.setOnClickListener {
            ActivityTrackerManager.sendStartLevelEvent(1, "beginner")
        }

        val btnSendEndLevelPassEvent = findViewById<Button>(R.id.btnSendEndLevelPassEvent)
        btnSendEndLevelPassEvent.setOnClickListener {
            ActivityTrackerManager.sendEndLevelEvent(1, "beginner", true)
        }

        val btnSendEndLevelFailedEvent = findViewById<Button>(R.id.btnSendEndLevelFailedEvent)
        btnSendEndLevelFailedEvent.setOnClickListener {
            ActivityTrackerManager.sendEndLevelEvent(1, "beginner", false)
        }

        val btnSendCustomEvent = findViewById<Button>(R.id.btnSendCustomEvent)
        btnSendCustomEvent.setOnClickListener {
            ActivityTrackerManager.sendCustomEvent("DummyEvent", mapOf("key" to "Hello world").toJsonString())
        }

        val btnSendManyEvents = findViewById<Button>(R.id.btnSendManyEvents)
        btnSendManyEvents.setOnClickListener {
            sendManyEvents()
        }
    }

    private fun sendManyEvents() {
        for (i in 1..100) { // Number of events you want to send
            ActivityTrackerManager.sendCustomEvent("DummyEvent", mapOf("eventNumber" to i).toJsonString())
        }
    }
}