package com.mashtalier.passwordmanager.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mashtalier.passwordmanager.R
import com.mashtalier.passwordmanager.databinding.HomePageBinding
import com.mashtalier.passwordmanager.viewmodel.HomeViewModel

class Home : Fragment() {

    private lateinit var binding: HomePageBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomePageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        initializeUI()
        return binding.root
    }

    private fun initializeUI() {
        configureSeekBar()
        configureCheckBoxes()
        configureButtons()
        updatePassword()
    }

    private fun configureSeekBar() {
        with(binding.passwordLengthSeekBar) {
            binding.passwordLengthLabel.text = getString(R.string.length_label, progress)

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    binding.passwordLengthLabel.text = getString(R.string.length_label, progress)
                    updatePassword()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun configureCheckBoxes() {
        val checkBoxListeners = listOf(
            binding.uppercaseOption,
            binding.numbersOption,
            binding.specialCharsOption
        )

        checkBoxListeners.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ -> updatePassword() }
        }
    }

    private fun configureButtons() {
        binding.generatePasswordButton.setOnClickListener { updatePassword() }

        binding.copyPasswordButton.setOnClickListener {
            binding.generatedPasswordView.text.toString().takeIf { it.isNotEmpty() }?.let { password ->
                copyToClipboard(password)
            }
        }
    }

    private fun updatePassword() {
        binding.generatedPasswordView.text = viewModel.createPassword(
            length = binding.passwordLengthSeekBar.progress,
            includeUppercase = binding.uppercaseOption.isChecked,
            includeNumbers = binding.numbersOption.isChecked,
            includeSpecialCharacters = binding.specialCharsOption.isChecked
        )
    }

    private fun copyToClipboard(password: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.generate_password), password)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), getString(R.string.password_copied), Toast.LENGTH_SHORT).show()
    }
}