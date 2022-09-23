package com.example.myapplication.di

import com.example.myapplication.ApiServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModul {


    @Provides
    fun providesUrl() = "http://3.80.40.156:8000/"

    @Provides
    @Singleton
    fun providesApiService(url: String): ApiServer =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServer::class.java)

}