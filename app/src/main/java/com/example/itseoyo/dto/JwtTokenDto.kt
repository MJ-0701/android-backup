package com.example.itseoyo.dto

import com.google.gson.annotations.SerializedName

data class JwtTokenDto(
    @SerializedName("code")
    val code : String?,

    @SerializedName("token")
    val token : String?
)