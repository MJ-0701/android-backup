package com.example.itseoyo.`object`

import android.util.Log
import com.example.itseoyo.NullOnEmptyConverterFactory
import com.example.itseoyo.retrofitinterface.PhoneInfoService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.lang.reflect.Type


object RetrofitObject {

//    private val apiInterface: PhoneInfoService
//    val retrofit:Retrofit
//    private const val baseUrl: String = "http://192.168.185.158:8080"
//    private var clientBuilder = OkHttpClient.Builder()            // 여기랑
//    private var loggingInterceptor = HttpLoggingInterceptor()    // 여기,
//
//
//    init {
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        clientBuilder.addInterceptor(loggingInterceptor)
//
//        retrofit = Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(clientBuilder.build())                // 여기를 추가해주면 된다.
//            .build()
//        apiInterface = retrofit.create(PhoneInfoService::class.java)
//    }
//
//    fun getApiInterface(): PhoneInfoService = apiInterface

    fun getRetrofitInstance(): Retrofit {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = clientBuilder.addInterceptor(loggingInterceptor).build()

        return Retrofit
            .Builder()
            .baseUrl("http://13.209.222.253:8000") // 로컬 테스트 : 현재 내 pc의 ip4 주소
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(nullOnEmptyConverterFactory)
            .client(client)
            .build();
    }

    private val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter =
                retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

            override fun convert(value: ResponseBody) =
                if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }

    fun okHttpClient(interceptor: AppInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }


    fun getCustomerApi(): PhoneInfoService {
        Log.d("레트로핏", "진입")
        return getRetrofitInstance().create(PhoneInfoService::class.java)
    }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "(header Value)")
                .build()
            proceed(newRequest)
        }
    }



}