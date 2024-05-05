package com.example.activity_tracker_sdk.core.tracker

import com.example.activity_tracker_sdk.data.models.Event

/**
 *
 * Interface of a tracker
 */
internal interface BaseTracker {
    fun trackEvent(event: Event)
    fun close()
}