package com.example.itseoyo.retrofitinterface

import com.example.itseoyo.dto.CodeDataDto
import com.example.itseoyo.dto.JwtTokenDto
import com.example.itseoyo.dto.PhoneInfoDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PhoneInfoService {

    @POST("/api/v1/customer/save")
    fun saveInfo(@Body phoneInfoDto: PhoneInfoDto?)

    @GET("/api/v1/customer/info")
    fun getCustomerInfo(@Query("id") id: Long?): Call<PhoneInfoDto>

//    @GET("/api/v1/customer/get-info")
//    fun getCustomerInfoByPhone(@Query("phoneNumber") phoneNumber : String?) : Call<PhoneInfoDto>

    @GET("/api/v1/customer/get-info")
    suspend fun getCustomerInfoByPhone(@Query("phoneNumber") phoneNumber: String?): Response<PhoneInfoDto>

//    @FormUrlEncoded
//    @POST("/api/call/info")
//    suspend fun getCustomerInfo(
//        @Field("phone") phone: String?,
//        @Header("Authorization") accessToken : String?
//        ): Response<PhoneInfoDto>

    @FormUrlEncoded
    @POST("/api/call/info")
    suspend fun getCustomerInfo(
        @Field("phone") phone: String?,
    ): Response<PhoneInfoDto>
    @GET("/api/getToken")
    suspend fun getJwtToken( ): Response<JwtTokenDto>
    @GET("/api/common/getCode")
    suspend fun getCodeData( ): Response<CodeDataDto>

    @GET("/api/tokenRefresh")
    suspend fun getRefreshToken(): Response<String>


}