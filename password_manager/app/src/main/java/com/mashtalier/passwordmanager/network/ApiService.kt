package com.mashtalier.passwordmanager.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class User(
    val full_name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ApiResponse(
    val success: Boolean,
    val message: String?,
    val user: User?,
    val error: String?
)

interface ApiService {
    @POST("register") // Update this endpoint based on your backend API
    fun registerUser(@Body user: User): Call<ApiResponse>

    @POST("login") // Login endpoint (this is the new one you're adding)
    fun loginUser(@Body loginRequest: LoginRequest): Call<ApiResponse>
}