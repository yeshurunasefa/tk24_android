package com.example.tech24.model

data class LoginResponse(
    val user: User,
    val token: String
)