package com.example.myapplication

data class PaymentRespons(
    val count: Int,
    val next: Any,
    val previous: Any,
    val results: List<ResultRespons>
)