package com.mikelcalvo.passwordgenerator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mashtalier.passwordmanager.R
import com.mashtalier.passwordmanager.data.PasswordItem

class PasswordAdapter(private val passwordList: List<PasswordItem>) :
    RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    class PasswordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_title)
        val password: TextView = view.findViewById(R.id.item_password)
        val date: TextView = view.findViewById(R.id.item_date)
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
        holder.date.text = passwordItem.date
    }

    override fun getItemCount(): Int {
        return passwordList.size
    }
}
