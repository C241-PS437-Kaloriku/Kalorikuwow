package com.dicoding.kaloriku.data.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.dicoding.kaloriku.BuildConfig

class ApiConfig {
    companion object {

        private const val BASE_URL = BuildConfig.BASE_URL

        fun getApiService(token: String? = null): ApiService {
            val loggingInterceptor = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            else
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)

            val clientBuilder = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)

            token?.let {
                clientBuilder.addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", it)
                        .build()
                    chain.proceed(newRequest)
                }
            }

            val client = clientBuilder.build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
