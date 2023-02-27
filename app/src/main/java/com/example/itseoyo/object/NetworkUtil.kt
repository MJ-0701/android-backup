package com.example.itseoyo.`object`

import com.example.itseoyo.GlobalApplication
import okhttp3.*
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.net.CookieManager


object NetworkUtil {

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

//    fun getRetrofitInstance(): Retrofit {
//        val clientBuilder = OkHttpClient.Builder()
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        val client = clientBuilder.addInterceptor(loggingInterceptor).build()
//
//        return Retrofit
//            .Builder()
//            .baseUrl("http://13.209.222.253:8000") // 로컬 테스트 : 현재 내 pc의 ip4 주소
//            .addConverterFactory(GsonConverterFactory.create())
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(nullOnEmptyConverterFactory)
//            .client(client)
//            .build();
//    }

    fun getRetrofitInstance(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://13.209.222.253:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(nullOnEmptyConverterFactory)
            .client(okHttpClient(AppInterceptor()))
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

    private fun okHttpClient(interceptor: AppInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            })
            .addInterceptor(interceptor)
//            .cookieJar(JavaNetCookieJar(CookieManager()))
            .build()
    }


//    fun getCustomerApi(): PhoneInfoService { // 코루틴 적용시 필요 없음. -> retrofit enqueue로 호출시 필요.
//        return getRetrofitInstance().create(PhoneInfoService::class.java)
//    }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
            val accessToken = GlobalApplication.prefs.getString("Authorization", "")

            val newRequest = request().newBuilder()
                .addHeader("Authorization", accessToken)
                .build()
            proceed(newRequest)
        }
    }



}