package com.example.activity_tracker_sdk.core

import android.content.Context
import java.lang.ref.WeakReference

internal object GlobalVariables {
    private var applicationContextReference: WeakReference<Context>? = null
    private var serverEndpoint: String? = null
    private var appId: Int? = null
    private var userId: String? = null
    private var eventBatchLimit: Int = 10
    private var sendDelayInMilliSeconds: Long = 0L

    fun setApplicationContext(context: Context) {
        val applicationContext = context.applicationContext ?: return

        applicationContextReference = WeakReference(applicationContext)
    }

    fun getApplicationContext(): Context? {
        return applicationContextReference?.get()
    }

    fun setServerEndpoint(endpoint: String) {
        serverEndpoint = endpoint
    }

    fun getServerEndpoint(): String? {
        return serverEndpoint
    }

    fun setAppId(appId: Int) {
        this.appId = appId
    }

    fun getAppId(): Int? {
        return appId
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun getUserId(): String? {
        return userId
    }

    fun setEventBatchLimit(eventBatchLimit: Int) {
        this.eventBatchLimit = eventBatchLimit
    }

    fun getEventBatchLimit(): Int {
        return eventBatchLimit
    }

    fun setSendDelayInMilliSeconds(sendDelayInMilliSeconds: Long) {
        this.sendDelayInMilliSeconds = sendDelayInMilliSeconds
    }

    fun getSendDelayInMilliSeconds(): Long {
        return sendDelayInMilliSeconds
    }

    fun reset() {
        applicationContextReference = null
        serverEndpoint = null
        appId = null
        userId = null
        eventBatchLimit = 0
        sendDelayInMilliSeconds = 0
    }
}