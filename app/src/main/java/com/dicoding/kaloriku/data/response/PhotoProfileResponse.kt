package com.dicoding.kaloriku.data.response

import com.google.gson.annotations.SerializedName

data class PhotoProfileResponse(

	@field:SerializedName("profilePictureUrl")
	val profilePictureUrl: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)