package com.example.itseoyo.dto

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class CustomerDto(

    @SerializedName("CODE")
    val code : String,

    @SerializedName("TYPE")
    val type : String,

    @SerializedName("NAME")
    val name : String,

    @SerializedName("PHONE")
    val phone : String,

    @SerializedName("itemNumber")
    val itemNumber: String,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("contents")
    val contents: String,

    @SerializedName("fileList")
    val fileList: MutableList<MultipartBody.Part?>
)