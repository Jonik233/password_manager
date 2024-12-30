package com.mashtalier.passwordmanager.data

class Generator {
    fun generate(length: Int, useUppercase: Boolean, useNumbers: Boolean, useSpecialChars: Boolean): String {
        val charPools = listOf(
            "abcdefghijklmnopqrstuvwxyz",
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ".takeIf { useUppercase }.orEmpty(),
            "0123456789".takeIf { useNumbers }.orEmpty(),
            "!@#\$%^&*()-_=+/<>?".takeIf { useSpecialChars }.orEmpty()
        )

        val passwordChars = charPools.joinToString("")

        return List(length) { passwordChars.random() }.joinToString("")
    }
}