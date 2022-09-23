package com.cheezycode.notesample.api


import com.example.myapplication.models.ForgotPass
import com.example.myapplication.models.UserRequest
import com.example.myapplication.models.UserResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("/rest-auth/registration/")
    suspend fun signup(@Body userRequest: UserRequest) : Response<UserResponse>

    @POST("/rest-auth/login/")
    suspend fun signin(@Body userRequest: UserRequest) : Response<UserResponse>

    @POST("/rest-auth/password/reset/")
    suspend fun forgotPassword(@Body userRequest: ForgotPass): Response<UserResponse>

    @POST("/rest-auth/logout/")
    suspend fun logOut(): ResponseBody

}