package com.example.itseoyo.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class CodeDataDto(

    @SerializedName("type1")
    val type1 : JsonObject,

    @SerializedName("type3")
    val type3 : JsonObject,

    @SerializedName("pay_type")
    val payType : JsonObject,

    @SerializedName("room_option")
    val roomOption : MutableList<String>,

    @SerializedName("show_range")
    val showRange : JsonObject,

    @SerializedName("status")
    val status : JsonObject,

    @SerializedName("sale_phone_relation")
    val salePhoneRelation : JsonObject

)
