package com.example.activity_tracker_sdk.data.models

import com.google.gson.annotations.SerializedName

internal data class RequestData(
    @SerializedName("app_Id") val appId: Int,
    @SerializedName("user_id") val userId: String,
    @SerializedName("events") val events: Array<Event>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestData

        if (appId != other.appId) return false
        if (userId != other.userId) return false
        if (!events.contentEquals(other.events)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = appId
        result = 31 * result + userId.hashCode()
        result = 31 * result + events.contentHashCode()
        return result
    }
}