package com.mashtalier.passwordmanager.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mashtalier.passwordmanager.LoginActivity
import com.mashtalier.passwordmanager.databinding.ProfilePageBinding
import com.mashtalier.passwordmanager.persistance.UserDataManager

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