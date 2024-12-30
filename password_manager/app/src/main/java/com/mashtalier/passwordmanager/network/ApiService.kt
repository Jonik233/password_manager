package com.mashtalier.passwordmanager.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class User(
    val id: Int,
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

data class PasswordItem(
    val user_id: Int,
    val title: String,
    val password: String
)

data class SavePasswordResponse(
    val message: String,
    val password_id: Int?
)

interface ApiService {
    @POST("register") // Update this endpoint based on your backend API
    fun registerUser(@Body user: User): Call<ApiResponse>

    @POST("login") // Login endpoint (this is the new one you're adding)
    fun loginUser(@Body loginRequest: LoginRequest): Call<ApiResponse>

    @POST("save_password")
    fun savePassword(@Body passwordItem: PasswordItem): Call<SavePasswordResponse>
}