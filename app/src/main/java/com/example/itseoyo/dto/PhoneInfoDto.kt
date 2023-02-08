package com.example.itseoyo.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class PhoneInfoDto(

    @SerializedName("code")
    val code : String?,

    @SerializedName("data")
    val data : UserInfo
)

data class UserInfo(

   @SerializedName("TYPE")
   val type : String?,

   @SerializedName("NAME")
   val name : String?,

   @SerializedName("PHONE")
   val phone : String?
)


