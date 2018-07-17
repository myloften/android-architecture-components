package com.loften.pagingsample.net

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitFactory{

    companion object {
        private var instance: RetrofitFactory? = null

        fun getInstance(): RetrofitFactory {
            if (instance == null){
                synchronized(RetrofitFactory::class.java){
                    if (instance == null){
                        instance = RetrofitFactory()
                    }
                }
            }
            return instance!!
        }
    }

    private val retrofit: Retrofit
    private val interceptor: Interceptor

    init {
        interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("charset", "utf-8")
                    .build()
            chain.proceed(request)
        }

        retrofit = Retrofit.Builder()
                .baseUrl(Contact.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(initClient())
                .build()
    }

    private fun initClient(): OkHttpClient{
        return OkHttpClient.Builder()
                .addInterceptor(initLogInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    private fun initLogInterceptor(): Interceptor{
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    fun <T> create(service: Class<T>): T{
        return retrofit.create(service)
    }

}