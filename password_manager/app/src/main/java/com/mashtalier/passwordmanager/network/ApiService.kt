package com.mashtalier.passwordmanager.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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
    val password_id: Int,
    val user_id: Int,
    val title: String,
    val password: String
)

data class SavePasswordResponse(
    val message: String,
    val password_id: Int
)

data class PasswordsResponse(
    val passwords: List<PasswordItem>
)

interface ApiService {
    @POST("register")
    fun registerUser(@Body user: User): Call<ApiResponse>

    @POST("login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<ApiResponse>

    @POST("save_password")
    fun savePassword(@Body passwordItem: PasswordItem): Call<SavePasswordResponse>

    @GET("get_passwords")
    fun getPasswords(@Query("user_id") userId: Int): Call<PasswordsResponse>

    @POST("delete_password")
    fun deletePassword(
        @Query("password_id") passwordId: Int,
        @Query("user_id") userId: Int
    ): Call<ApiResponse>
}