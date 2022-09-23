package com.example.myapplication


import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiServer {

    @GET("/payment/api/v1/pricing-list/")
    suspend fun getPost(): Response<PaymentRespons>


}