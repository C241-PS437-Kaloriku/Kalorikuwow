package com.dicoding.kaloriku.data.response

import com.dicoding.kaloriku.data.pref.UserPreference
import com.google.gson.annotations.SerializedName

data class UserProfile(
	@SerializedName("birthdate")
	val birthdate: String? = null,

	@SerializedName("gender")
	val gender: String,

	@SerializedName("weight")
	val weight: Int? = null,

	@SerializedName("userId")
	val userId: String? = null,

	@SerializedName("email")
	val email: String? = null,

	@SerializedName("age")
	val age: Int? = null,

	@SerializedName("username")
	val username: String? = null,

	@SerializedName("height")
	val height: Int? = null
)

data class ProfileResponse(
	@SerializedName("message")
	val message: String? = null,

	@SerializedName("user")
	val user: UserProfile? = null
)


