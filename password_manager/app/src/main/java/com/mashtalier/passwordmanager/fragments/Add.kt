package com.mashtalier.passwordmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mashtalier.passwordmanager.PasswordRegistry
import com.mashtalier.passwordmanager.R

class Add : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.add_page, container, false)

        val addPasswordButton = view.findViewById<Button>(R.id.addPasswordButton)
        addPasswordButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, PasswordRegistry())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }
}
