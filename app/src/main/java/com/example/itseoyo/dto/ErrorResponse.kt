package com.example.itseoyo.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ErrorResponse(

    @SerializedName("resultCode")
    var resultCode: String? = null,

    @SerializedName("httpStatus")
    var httpStatus: String? = null,

    @SerializedName("httpMethod")
    var httpMethod: String? = null,

    @SerializedName("message")
    var message: String? = null,

    var path: String? = null,

    var timestamp: LocalDateTime? = null,

    var errors: MutableList<ErrorField>? = null


)

data class ErrorField(
    var field: String? = null,
    var message: String? = null,
    var value: Any? = null
)
