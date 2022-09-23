package com.example.myapplication.di

import com.cheezycode.notesample.api.AuthInterceptor
import com.cheezycode.notesample.api.UserAPI
import com.example.myapplication.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit.Builder {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun providesUserAPI(retrofitBuilder: Retrofit.Builder): UserAPI {
        return retrofitBuilder.build().create(UserAPI::class.java)
    }

   /* @Singleton
    @Provides
    fun providePaymentApi(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): PaymentApi{
        return retrofitBuilder.client(okHttpClient).build().create(PaymentApi::class.java)
    }*/

  /*  @Singleton
    @Provides
    fun providesNoteAPI(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): PaymentApi {
        return retrofitBuilder.client(okHttpClient).build().create(PaymentApi::class.java)
    }*/


}