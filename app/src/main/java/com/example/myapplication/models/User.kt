package com.example.myapplication.models

data class User(
    val avatar: Any,
    val email: String,
    val first_name: String,
    val is_subscription_valid: Boolean,
    val last_name: String,
    val pk: Int,
    val preffered_language: List<Any>,
    val preffered_language_details: List<Any>,
    val subcription_expires: Any,
    val username: String
)