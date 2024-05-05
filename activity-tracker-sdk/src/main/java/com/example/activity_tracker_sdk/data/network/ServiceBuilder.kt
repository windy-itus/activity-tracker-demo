package com.example.activity_tracker_sdk.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class ServiceBuilder {
    private val mRetrofitBuilder = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create()
        )

    private val mHttpClientBuilder = OkHttpClient.Builder()

    fun setBaseUrl(baseUrl: String): ServiceBuilder {
        mRetrofitBuilder.baseUrl(baseUrl)

        return this
    }

    fun <S> createService(serviceClass: Class<S>): S {
        mHttpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
        mHttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        mHttpClientBuilder.writeTimeout(30, TimeUnit.SECONDS)

        // TODO: Should check build type before add logger
        mHttpClientBuilder.addInterceptor(
            HttpLoggingInterceptor { message ->
                Log.d(
                    serviceClass.simpleName,
                    message.replace("%", "%%")
                )
            }.setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        return mRetrofitBuilder
            .client(mHttpClientBuilder.build())
            .build().create(serviceClass)
    }
}