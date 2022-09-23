package com.example.myapplication.repository


import com.example.myapplication.ApiServer
import com.example.myapplication.PaymentRespons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

import javax.inject.Inject
import kotlin.Result

class PostRepository
    @Inject
    constructor(private val apiServer: ApiServer){

        fun getPost(): Flow<Response<PaymentRespons>> = flow {
            emit(apiServer.getPost())
        }.flowOn(Dispatchers.IO)


}