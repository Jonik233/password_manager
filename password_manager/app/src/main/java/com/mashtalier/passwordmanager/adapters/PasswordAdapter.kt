package com.mashtalier.passwordmanager.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mashtalier.passwordmanager.R
import com.mashtalier.passwordmanager.fragments.Add
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
                    handleEditAction(position)
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

    private fun handleEditAction(position: Int) {
        val passwordItem = passwordList[position]
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