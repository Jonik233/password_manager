package com.mashtalier.passwordmanager.persistance

import android.content.Context
import android.content.SharedPreferences

class UserDataManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_USER_ID = "id"
    }

    // Save user data to SharedPreferences
    fun saveUserData(userId: Int, fullName: String, email: String) {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_FULL_NAME, fullName)
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    // Retrieve the user ID
    fun getUserId(): Int? {
        val userId = sharedPreferences.getInt(KEY_USER_ID, -1)
        return if (userId == -1) null else userId
    }

    // Retrieve the full name
    fun getFullName(): String {
        return sharedPreferences.getString(KEY_FULL_NAME, "Default Name") ?: "Default Name"
    }

    // Retrieve the email
    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "Default Email") ?: "Default Email"
    }

    // Clear all user data
    fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_FULL_NAME)
        editor.remove(KEY_EMAIL)
        editor.apply()
    }
}
