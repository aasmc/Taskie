package ru.aasmc.taskie.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

fun buildClient(): OkHttpClient =
    OkHttpClient.Builder()
        .build()

@ExperimentalSerializationApi
fun buildRetrofit(): Retrofit {
    val contentType = "application/json".toMediaType()
    return Retrofit.Builder()
        .client(buildClient())
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()
}

@ExperimentalSerializationApi
fun buildApiService(): RemoteApiService =
    buildRetrofit().create(RemoteApiService::class.java)