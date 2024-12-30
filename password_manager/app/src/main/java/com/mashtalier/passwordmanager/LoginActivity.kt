package com.mashtalier.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mashtalier.passwordmanager.network.ApiService
import com.mashtalier.passwordmanager.network.LoginRequest
import com.mashtalier.passwordmanager.network.ApiResponse
import com.mashtalier.passwordmanager.network.RetrofitClient
import com.mashtalier.passwordmanager.persistance.UserDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button

    private lateinit var userDataManager: UserDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        signUpButton = findViewById(R.id.signup_button)
        loginButton = findViewById(R.id.login_button)

        userDataManager = UserDataManager(this)

        signUpButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        apiService.loginUser(loginRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    if (user != null) {
                        val userId = user.id
                        val fullName = user.full_name
                        val email = user.email

                        // Save user data
                        userDataManager.saveUserData(userId, fullName, email)

                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                        // Redirect to MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "User data is missing", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = response.body()?.error ?: "Login failed"
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}