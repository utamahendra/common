package com.example.core.di

import com.example.core.BuildConfig
import com.example.core.Timeout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val coreNetworkModule = module {
    single { GsonBuilder().setLenient().create() }

    single(named("logging")) {
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        } as Interceptor
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>(named("logging")))
            .readTimeout(Timeout.GENERAL_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(Timeout.GENERAL_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
    }
}

inline fun <reified T> provideRetrofit(okHttpClient: OkHttpClient, gson: Gson, baseUrl: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    return retrofit.create(T::class.java)
}