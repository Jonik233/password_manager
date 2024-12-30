package com.mashtalier.passwordmanager.persistance

import android.content.Context
import android.content.SharedPreferences

class UserDataManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
    }

    // Save user data to SharedPreferences
    fun saveUserData(fullName: String, email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_FULL_NAME, fullName)
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getFullName(): String {
        return sharedPreferences.getString(KEY_FULL_NAME, "Default Name") ?: "Default Name"
    }

    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "Default Email") ?: "Default Email"
    }

    fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_FULL_NAME)
        editor.remove(KEY_EMAIL)
        editor.apply()
    }
}
