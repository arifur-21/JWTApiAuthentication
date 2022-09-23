package com.example.myapplication.models

data class UserRequest(
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String
)