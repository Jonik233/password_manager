package com.mashtalier.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mashtalier.passwordmanager.network.ApiService
import com.mashtalier.passwordmanager.network.RetrofitClient
import com.mashtalier.passwordmanager.network.User
import com.mashtalier.passwordmanager.network.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RegistrationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)

        val fullNameInput: EditText = findViewById(R.id.full_name_input)
        val emailInput: EditText = findViewById(R.id.email_input)
        val passwordInput: EditText = findViewById(R.id.password_input)
        val repeatPasswordInput: EditText = findViewById(R.id.repeat_password_input)
        val signUpButton: Button = findViewById(R.id.signup_button)
        val backButton: Button = findViewById(R.id.back_button)

        // Retrofit API service
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        // Back button logic
        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Sign-up button logic
        signUpButton.setOnClickListener {
            val fullName = fullNameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val repeatPassword = repeatPasswordInput.text.toString()

            // Validate inputs
            if (fullName.isBlank() || email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
                Log.e(TAG, "All fields are required!")
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Log.e(TAG, "Passwords do not match!")
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Make API call
            val user = User(fullName, email, password)
            apiService.registerUser(user).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val message = response.body()?.message ?: "Registration successful!"
                        Log.i(TAG, message)
                        Toast.makeText(this@RegistrationActivity, message, Toast.LENGTH_SHORT).show()

                        // Redirect to LoginActivity
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e(TAG, "Registration failed: $errorMessage")

                        // Handle error responses like duplicate email or invalid data
                        if (errorMessage.contains("duplicate key value", true)) {
                            Toast.makeText(this@RegistrationActivity, "Email is already used. Please try another.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@RegistrationActivity, "Registration failed: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e(TAG, "Error: ${t.message}", t)
                    Toast.makeText(this@RegistrationActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}