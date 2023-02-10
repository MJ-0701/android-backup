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

   @SerializedName("ADDR")
   val address : String?,

   // 코드 데이터 타입
   @SerializedName("TYPE1")
   val type1 : String?,

   @SerializedName("TYPE2")
   val type2 : String?,

   @SerializedName("TYPE3")
   val type3 : String?,

   @SerializedName("BD_NAME")
   val bdName : String?, // 건물명

   @SerializedName("ROOM_HO")
   val roomHo : String?, // 호실

   @SerializedName("STATUS")
   val status : String?, // 상태

   @SerializedName("PAY_TYPE")
   val payType : String?, // 매매/전세/월세

   @SerializedName("PRICE")
   val price : String?, // 가격

   @SerializedName("SALE_UUID")
   val saleUuid : String?, // 건물 번호

   @SerializedName("NAME")
   val name : String?,

   @SerializedName("PHONE")
   val phone : String?,

   @SerializedName("COMMENT")
   val comment : String?

)



