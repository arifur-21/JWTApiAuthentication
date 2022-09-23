package com.example.myapplication.util

import com.example.myapplication.PaymentRespons
import retrofit2.Response


sealed class ApiState {

    object Loading: ApiState()
    class Failure(val msg: Throwable) : ApiState()
    class Success(val data : Response<PaymentRespons>): ApiState()
    object Empty : ApiState()
}