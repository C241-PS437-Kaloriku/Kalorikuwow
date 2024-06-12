package com.dicoding.kaloriku.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean,
    val userId: String
)