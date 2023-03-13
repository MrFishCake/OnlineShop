package com.example.onlineshop.models

class User(
    private var firstName: String,
    private var secondName: String,
    private var balance: Int,
    private var avatar: String,
    private var latest: String,
    private var email: String,
    private var pass: String
) {
    fun getAvatar(): String { return avatar }

    fun getFirstName(): String { return firstName }

    fun getSecondName(): String { return secondName }

    fun getBalance(): Int { return balance }

    fun getLatest(): String { return latest }

    fun getEmail(): String { return email }

    fun getPass(): String { return pass }
}