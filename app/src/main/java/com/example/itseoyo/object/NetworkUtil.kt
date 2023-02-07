package com.example.itseoyo.`object`

import com.example.itseoyo.dto.ErrorResponse
import okhttp3.ResponseBody

object NetworkUtil {

    fun getErrorResponse(errorBody: ResponseBody): ErrorResponse {
//        return RetrofitObject.retrofit.responseBodyConverter<ErrorResponse>(ErrorResponse::class.java, ErrorResponse::class.java.annotations).convert(errorBody)!!
        return RetrofitObject.getRetrofitInstance()
            .responseBodyConverter<ErrorResponse>(
                ErrorResponse::class.java,
                ErrorResponse::class.java.annotations
            ).convert(errorBody)!!
    }
}