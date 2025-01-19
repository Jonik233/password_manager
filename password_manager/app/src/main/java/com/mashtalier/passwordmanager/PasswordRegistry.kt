package com.mashtalier.passwordmanager

import android.util.Log
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mashtalier.passwordmanager.fragments.Add
import com.mashtalier.passwordmanager.network.ApiService
import com.mashtalier.passwordmanager.network.PasswordItem
import com.mashtalier.passwordmanager.network.RetrofitClient
import com.mashtalier.passwordmanager.persistance.UserDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PasswordRegistry : Fragment() {

    private lateinit var userDataManager: UserDataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.password_page, container, false)

        userDataManager = UserDataManager(requireContext())

        val titleInput = view.findViewById<EditText>(R.id.title_input)
        val passwordInput = view.findViewById<EditText>(R.id.password_input)
        val saveButton = view.findViewById<Button>(R.id.register_button)

        saveButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (title.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            savePasswordItem(title, password)
        }

        return view
    }

    private fun savePasswordItem(title: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = userDataManager.getUserId()

            if (userId != null) {
                val passwordItem = PasswordItem(password_id = 0, user_id = userId, title = title, password = password)

                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                try {
                    val response = apiService.savePassword(passwordItem).execute()

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body()?.password_id != null) {
                            Toast.makeText(requireContext(), "Password saved successfully!", Toast.LENGTH_SHORT).show()

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, Add())
                                .addToBackStack(null)
                                .commit()
                        } else {
                            Toast.makeText(requireContext(), "Failed to save password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}