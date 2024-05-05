package com.example.activity_tracker_sdk.core

import android.content.Context
import android.content.res.Resources
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import com.example.activity_tracker_sdk.core.tracker.BaseTracker
import com.example.activity_tracker_sdk.core.tracker.NetworkTracker
import com.example.activity_tracker_sdk.data.models.Event
import com.example.activity_tracker_sdk.utils.Constant.SHARED_PREFS_FILE
import com.example.activity_tracker_sdk.utils.Constant.USER_ID_KEY
import com.example.activity_tracker_sdk.utils.convertJsonToMap
import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/**
 * Setting of SDK
 * SDK to track user's activity in app
 * Responsible for contact point of SDK.
 */
object ActivityTrackerManager {

    private var initialized = false
    private const val version = "1.0.0" // SDK version
    private lateinit var tracker: BaseTracker

    /**
     * Initialize SDK [ActivityTrackerManager]] with initialization options.
     * This function must be invoked as first shot to SDK.
     *
     * @param   options instance of [ActivityTrackerInitOptions].
     *
     * @throws IllegalArgumentException if [ActivityTrackerInitOptions.applicationContext] is not the context of the
     * application.
     */
    @Throws(IllegalArgumentException::class)
    fun initialize(options: ActivityTrackerInitOptions) {
        Log.i("ActivityTrackerManager", "initialize")
        if (initialized) {
            return
        }
        if (options.applicationContext !== options.applicationContext.applicationContext) {
            throw IllegalArgumentException("Invalid application context.")
        }

        GlobalVariables.setApplicationContext(options.applicationContext)
        GlobalVariables.setServerEndpoint(options.serverEndpoint)
        GlobalVariables.setAppId(options.appId)
        GlobalVariables.setEventBatchLimit(options.eventBatchLimit)
        GlobalVariables.setSendDelayInMilliSeconds(options.sendDelayInMilliSeconds)

        // Get user id
        val existingUserId = getUserIdFromDevice()
        val isNewUserId = existingUserId == null
        GlobalVariables.setUserId(existingUserId ?: createAndStoreNewUserId())

        // init tracker
        tracker = NetworkTracker()

        // send open event
        sendOpenEvent(isNewUserId)

        // Mark SDK initialized
        initialized = true
    }

    /**
     * Shutdown the SDK.
     * You are not be able to use the SDK after calling this function.
     * All current operations will be terminate
     */
    fun shutdown() {
        Log.i("ActivityTrackerManager", "shutdown")
        tracker.close()
        GlobalVariables.reset()
        initialized = false
    }

    /**
     * Track start level event.
     * @param levelId id of the level.
     * @param levelName name of the level.
     * @throws IllegalStateException if [initialize] was not invoked before.
     */
    fun sendStartLevelEvent(levelId: Int, levelName: String) {
        ifInitialized {
            val eventData = mapOf("level_id" to levelId, "level_name" to levelName)
            val eventName = "start_level"
            val event = Event(name = eventName, data = eventData)
            tracker.trackEvent(event)
        }
    }

    /**
     * Track end level event.
     * @param levelId id of the level.
     * @param levelName name of the level.
     * @param passed a flag that indicates if the user has passed the level or not
     * @throws IllegalStateException if [initialize] was not invoked before.
     */
    fun sendEndLevelEvent(levelId: Int, levelName: String, passed: Boolean) {
        ifInitialized {
            val eventData = mapOf("level_id" to levelId, "level_name" to levelName)
            val eventName = if (passed) "complete_level" else "fail_level"
            val event = Event(name = eventName, data = eventData)
            tracker.trackEvent(event)
        }
    }

    /**
     * Track end level event.
     * @param eventName name of event.
     * @param eventDataJson JSON object of the event data in string.
     * @throws IllegalStateException if [initialize] was not invoked before.
     */
    fun sendCustomEvent(eventName: String, eventDataJson: String) {
        ifInitialized {
            // Convert JSON string to a Map and send the event
            val eventData = convertJsonToMap(eventDataJson)
            val event = Event(name = eventName, data = eventData)
            tracker.trackEvent(event)
        }
    }

    // Private methods region

    /**
     * Called automatically by the SDK when initialized.
     * We need to collect some device info and send them along with the event.
     * Invoked [initialize]
     */
    // Send the open event to the server
    // Don't need to check Initialized due to this was invoked in `Initialized`
    private fun sendOpenEvent(isNewUser: Boolean) {
        // Application context have to available if `initialize` method was invoked
        val context = GlobalVariables.getApplicationContext()
            ?: throw IllegalStateException("Application context not available")

        // Create the event data
        val deviceInfo = getDeviceInformation(context)
        val openEventData = deviceInfo + mapOf("version" to version)
        val openEventName = if (isNewUser) "first_open" else "open"
        val openEvent = Event(name = openEventName, data = openEventData)

        tracker.trackEvent(openEvent)
    }

    // Retrieve the user ID from device storage, return null if not found
    private fun getUserIdFromDevice(): String? {
        val context = GlobalVariables.getApplicationContext() ?: return null
        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID_KEY, null)
    }

    // Create a new UUID and store it in the device storage
    private fun createAndStoreNewUserId(): String {
        val newUserId = UUID.randomUUID().toString()
        // Application context have to available if `initialize` method was invoked
        val context = GlobalVariables.getApplicationContext()
            ?: throw IllegalStateException("Application context not available. Call initialize() first.")

        val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(USER_ID_KEY, newUserId).apply()
        return newUserId
    }

    private fun ifInitialized(action: () -> Unit) {
        if (!initialized) {
            throw IllegalStateException("SDK not initialized. Call initialize() first.")
        }
        action()
    }

    // End private methods region

    // Helper regions
    // TODO: Consider move these methods in another utility class

    private fun getDeviceInformation(context: Context): Map<String, Any> {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
        val locale = Locale.getDefault().toString()
        val timezone = TimeZone.getDefault().id
        val ipAddress = getLocalIpAddress(context) ?: ""

        return mapOf(
            "bundle_id" to context.packageName,
            "app_version" to packageInfo.versionName,
            "app_build" to packageInfo.versionCode.toString(),
            "platform" to "Android",
            "os_version" to Build.VERSION.RELEASE,
            "screen_width" to Resources.getSystem().displayMetrics.widthPixels,
            "screen_height" to Resources.getSystem().displayMetrics.heightPixels,
            "locale" to locale,
            "timezone" to timezone,
            "ip_address" to ipAddress,
        )
    }

    private fun getLocalIpAddress(context: Context): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress

        // Convert integer IP address to byte array
        val byteBuffer = ByteBuffer.allocate(4)
        byteBuffer.putInt(ipAddress)
        val ipAddressBytes = byteBuffer.array().reversedArray()

        // Convert the byte array to string format
        try {
            return InetAddress.getByAddress(ipAddressBytes).hostAddress
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Unavailable"
    }
    // End helper regions
}