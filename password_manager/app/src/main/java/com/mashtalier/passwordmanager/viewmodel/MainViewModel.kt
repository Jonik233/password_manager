package com.mashtalier.passwordmanager.viewmodel

import androidx.lifecycle.ViewModel
import com.mashtalier.passwordmanager.data.Generator

class HomeViewModel : ViewModel() {
    private val generator = Generator()

    fun createPassword(length: Int, includeUppercase: Boolean, includeNumbers: Boolean, includeSpecialCharacters: Boolean): String {
        return generator.generate(length, includeUppercase, includeNumbers, includeSpecialCharacters)
    }
}
