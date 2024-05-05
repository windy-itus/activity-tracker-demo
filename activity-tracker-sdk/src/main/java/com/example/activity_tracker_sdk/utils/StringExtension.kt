package com.example.activity_tracker_sdk.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

fun Map<String, Any>.toJsonString(): String {
    return Gson().toJson(this)
}

fun convertJsonToMap(jsonString: String): Map<String, Any> {
    return try {
        Gson().fromJson(jsonString, object : TypeToken<Map<String, Any>>() {}.type)
    } catch (e: JsonSyntaxException) {
        emptyMap()
    }
}