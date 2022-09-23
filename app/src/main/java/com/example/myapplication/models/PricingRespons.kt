package com.example.myapplication.models

data class PricingRespons(
    val created_at: String,
    val currency: String,
    val id: Int,
    val interval: String,
    val name: String,
    val price: String,
    val updated_at: String
)