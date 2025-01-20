package com.mashtalier.passwordmanager.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.mashtalier.passwordmanager.R
import com.mashtalier.passwordmanager.network.ApiResponse
import com.mashtalier.passwordmanager.network.ApiService
import com.mashtalier.passwordmanager.network.PasswordItem
import com.mashtalier.passwordmanager.network.RetrofitClient
import com.mashtalier.passwordmanager.persistance.UserDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordAdapter(private val passwordList: MutableList<PasswordItem>, private val listener: PasswordActionListener) :
    RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    class PasswordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_title)
        val password: TextView = view.findViewById(R.id.item_password)
        val editButton: View = view.findViewById(R.id.edit_button)
        val context = view.context
    }

    interface PasswordActionListener {
        fun onPasswordDeleted()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val passwordItem = passwordList[position]
        holder.title.text = passwordItem.title
        holder.password.text = passwordItem.password

        holder.editButton.setOnClickListener { view ->
            showPopupMenu(view, holder, position)
        }
    }

    private fun showPopupMenu(view: View, holder: PasswordViewHolder, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.edit_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    handleEditAction(holder, position)
                    true
                }
                R.id.action_delete -> {
                    handleDeleteAction(holder, position)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun handleEditAction(holder: PasswordViewHolder, position: Int) {
        val passwordItem = passwordList[position]
        val context = holder.context

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_password, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.edit_title)
        val passwordInput = dialogView.findViewById<EditText>(R.id.edit_password)

        titleInput.setText(passwordItem.title)
        passwordInput.setText(passwordItem.password)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Password")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = titleInput.text.toString().trim()
                val newPassword = passwordInput.text.toString().trim()

                if (newTitle.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                val userId = UserDataManager(context).getUserId()

                if (userId == null) {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedPasswordItem = passwordItem.copy(title = newTitle, password = newPassword)

                apiService.updatePassword(passwordItem.password_id, userId, updatedPasswordItem)
                    .enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                            if (response.isSuccessful && response.body()?.success == true) {
                                passwordList[position] = updatedPasswordItem
                                notifyItemChanged(position)
                                Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                            } else {
                                val errorMessage = response.body()?.message ?: "Unknown error"
                                Toast.makeText(context, "Failed to update password: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


    private fun handleDeleteAction(holder: PasswordViewHolder, position: Int) {
        Log.e("ItemPosition", "Position: $position")
        val passwordItem = passwordList[position]
        val context = holder.context
        val userId = UserDataManager(context).getUserId()

        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        apiService.deletePassword(passwordItem.password_id, userId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    passwordList.removeAt(position)
                    notifyItemRemoved(position)
                    listener.onPasswordDeleted()
                    Toast.makeText(context, "Password deleted", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMessage = response.body()?.message ?: "Unknown error"
                    Toast.makeText(context, "Failed to delete password: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int {
        return passwordList.size
    }
}