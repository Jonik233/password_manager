package com.mashtalier.passwordmanager.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mashtalier.passwordmanager.PasswordRegistry
import com.mashtalier.passwordmanager.R
import com.mashtalier.passwordmanager.adapters.PasswordAdapter
import com.mashtalier.passwordmanager.network.PasswordsResponse
import com.mashtalier.passwordmanager.network.RetrofitClient
import com.mashtalier.passwordmanager.persistance.UserDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Add : Fragment(), PasswordAdapter.PasswordActionListener {

    lateinit var passwordAdapter: PasswordAdapter
    private val passwordList = mutableListOf<com.mashtalier.passwordmanager.network.PasswordItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.add_page, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.password_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        passwordAdapter = PasswordAdapter(passwordList, this)
        recyclerView.adapter = passwordAdapter

        fetchPasswords()

        val addPasswordButton = view.findViewById<Button>(R.id.addPasswordButton)
        addPasswordButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, PasswordRegistry())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    private fun fetchPasswords() {
        val context = requireContext()
        val userDataManager = UserDataManager(context)
        val userId = userDataManager.getUserId()

        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            Log.e("FetchPasswords", "User not logged in")
            return
        }

        val apiService = RetrofitClient.instance.create(com.mashtalier.passwordmanager.network.ApiService::class.java)

        apiService.getPasswords(userId).enqueue(object : Callback<PasswordsResponse> {
            override fun onResponse(
                call: Call<PasswordsResponse>,
                response: Response<PasswordsResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    passwordList.clear()
                    passwordList.addAll(response.body()!!.passwords)
                    passwordAdapter.notifyDataSetChanged()
                    Log.e("PasswordList", "$passwordList")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(context, "Failed to load passwords", Toast.LENGTH_SHORT).show()
                    Log.e("FetchPasswords", "API call failed with response: $errorMessage")
                }
            }

            override fun onFailure(call: Call<PasswordsResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("FetchPasswords", "Network call failed", t)
            }

        })
    }

    override fun onPasswordDeleted() {
        fetchPasswords()
    }
}