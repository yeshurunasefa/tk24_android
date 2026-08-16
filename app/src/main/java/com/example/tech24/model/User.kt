package com.example.tech24.model

data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val gender: String,
    val phonenumber: String,
    val email: String,
    val address: String,
    val is_user_active: Boolean,
    val email_verified_at: String?,
    val first_time: Int,
    val created_at: String?,
    val updated_at: String?,
    val old_user_id: Int?
)