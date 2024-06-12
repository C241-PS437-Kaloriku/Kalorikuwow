package com.dicoding.kaloriku.data.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class BMIResponse(
	val message: String,
	val bmi: String,
	val category: String
)

