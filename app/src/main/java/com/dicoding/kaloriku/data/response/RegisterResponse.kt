package com.dicoding.kaloriku.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null
)

data class RegisterRequest(

	@SerializedName("email")
	val email: String,

	@SerializedName("password")
	val password: String,

	@SerializedName("birthdate")
	val birthdate: String
)
