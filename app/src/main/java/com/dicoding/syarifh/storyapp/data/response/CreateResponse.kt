package com.dicoding.syarifh.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class CreateResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
