package com.example.itseoyo.retrofitinterface

import com.example.itseoyo.dto.CustomerDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface CustomerService {


    @POST("/api/v1/customer/save")
    suspend fun saveCustomerInfo(@Body params: Map<String, String>)

//    @POST("/api/v1/customer/save")
//    suspend fun saveCustomerInfo(@Body params : Map<String, String>) : Response<CustomerDto>


    @GET("/api/v1/customer/get-info")
    suspend fun getCustomerInfoByPhone(@Query("phoneNumber") phoneNumber: String?): Response<CustomerDto>

    @GET("/api/call/info")
    suspend fun getCustomerInfo(@Query("phoneNumber") phoneNumber: String?): Response<CustomerDto>

//    @Multipart
//    @POST("/api/v1/customer/upload-customer-info")
//    fun saveCustomerInfo(
//        @Part fileList : MutableList<MultipartBody.Part?>,
//        @Part itemNumber : MultipartBody.Part,
//        @Part userName : MultipartBody.Part,
//        @Part phoneNumber : MultipartBody.Part,
//        @Part contents : MultipartBody.Part
//
//    ) : Call<CustomerDto>

    @Multipart
    @POST("/api/v1/customer/upload-customer-info")
    suspend fun uploadCustomerInfo(
        @Part fileList: MultipartBody.Part,
        @PartMap requestBody: MultipartBody.Part
    ): Response<CustomerDto>


}