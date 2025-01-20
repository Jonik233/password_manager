package com.mashtalier.passwordmanager.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mashtalier.passwordmanager.LoginActivity
import com.mashtalier.passwordmanager.databinding.ProfilePageBinding
import com.mashtalier.passwordmanager.network.ApiService
import com.mashtalier.passwordmanager.network.PasswordsResponse
import com.mashtalier.passwordmanager.network.RetrofitClient
import com.mashtalier.passwordmanager.persistance.UserDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : Fragment() {

    private var _binding: ProfilePageBinding? = null
    private val binding get() = _binding!!

    private lateinit var userDataManager: UserDataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfilePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDataManager = UserDataManager(requireContext())

        val fullName = userDataManager.getFullName()
        val email = userDataManager.getEmail()

        binding.fullName.text = fullName
        binding.email.text = email

        val userId = userDataManager.getUserId()
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        if (userId != null) {
            apiService.getPasswords(userId).enqueue(object : Callback<PasswordsResponse> {
                override fun onResponse(call: Call<PasswordsResponse>, response: Response<PasswordsResponse>) {
                    if (response.isSuccessful) {
                        val numPasswords = response.body()?.passwords?.size ?: 0
                        binding.numberSavedPasswords.text = "Number of passwords: ${numPasswords}"
                    } else {
                        binding.numberSavedPasswords.text = "Error"
                    }
                }

                override fun onFailure(call: Call<PasswordsResponse>, t: Throwable) {
                    binding.numberSavedPasswords.text = "Error"
                }
            })
        } else {
            binding.numberSavedPasswords.text = "0"
        }

        binding.logoutButton.setOnClickListener {
            userDataManager.clearUserData()

            // Redirect to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}