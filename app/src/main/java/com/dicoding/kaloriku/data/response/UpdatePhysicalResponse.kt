package com.dicoding.kaloriku.data.response

import com.google.gson.annotations.SerializedName

data class UpdatePhysicalResponse(

	@field:SerializedName("message")
	val message: String? = null
)

data class UpdatePhysicalRequest(

	@SerializedName("weight")
	val weight: Int,

	@SerializedName("height")
	val height: Int,

	@SerializedName("gender")
	val gender: String,

	@SerializedName("birthdate")
	val birthdate: String,

	@SerializedName("userId")
	val userId: String,

	@SerializedName("name")
	val name: String="",

)
