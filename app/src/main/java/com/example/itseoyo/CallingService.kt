package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.itseoyo.`object`.RetrofitObject
import com.example.itseoyo.retrofitinterface.PhoneInfoService
import kotlinx.coroutines.*
import retrofit2.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class CallingService : Service() {

    private var mWindowManager: WindowManager? = null
    private var windowManager: WindowManager? = null
    var params: WindowManager.LayoutParams? = null
    var rootView: View? = null
    val channelId: String = "com.example.itseoyo"
    val channelName: String = "Service Channel"

    val NOTIFICATION_ID = 1234

    private var mTouchX = 0f
    private var mTouchY: Float? = 0f
    private var mViewX = 0
    private var mViewY: Int? = 0
    private var isMove = false


    // 최상단 표기 데이터
    var nameTxt : TextView? = null

    // 전화번호
    var phoneTxt: TextView? = null

    // 유형
    var category: TextView? = null

    // 유형 옆 소유주 이름
    var rightOwner: TextView? = null

    // 상태
    var statusType: TextView? = null

    // 가격
    var price: TextView? = null

    // 주소
    var address: TextView? = null

    // 물건 번호
    var itemNumber: TextView? = null

    // 소유주(이름)
    var owner: TextView? = null

    // 노트
    var note: TextView? = null

    // 특징
    var specialFeature: TextView? = null

    var itemText: TextView? = null

    var contentTxt: TextView? = null

    private var popupFlag : Boolean = false

    private val service = RetrofitObject.getRetrofitInstance().create(PhoneInfoService::class.java)

    @SuppressLint("PackageManagerGetSignatures")
    private fun getHashKey() {

        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
        for (signature in packageInfo!!.signatures) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", android.util.Base64.encodeToString(md.digest(), android.util.Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreate() {
        Log.d("서비스", "확인")
        if(Settings.canDrawOverlays(this)) {
            Log.d("오버레이 퍼미션", "확인")
        }else {
            Log.d("오버레이 퍼미션", "없음")
        }
        getHashKey()
        createNotification()
        super.onCreate()

    }

    @SuppressLint("InflateParams")
    private fun createView(popupFlag: Boolean, customerCategory : String?) {
        val mInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        if(popupFlag) {
            Log.d("뷰생성", customerCategory.toString())

            if(customerCategory == "SALE") {
                rootView = mInflater.inflate(R.layout.scroll_view, null)
            }else if(customerCategory == "CUSTOMER") {
                rootView = mInflater.inflate(R.layout.customer_scroll_view, null)
            }

            val layoutParam : Int = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParam,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,

                PixelFormat.TRANSLUCENT
            )

            params!!.gravity = Gravity.CENTER
            params!!.x = 0;
            params!!.y = 100;

            mWindowManager!!.addView(rootView, params)
            rootView!!.setOnTouchListener(mViewTouchListener)
            rootView!!.findViewById<View>(R.id.cancel_button).setOnClickListener {
                onDestroy()
            }

        }else {
            rootView = mInflater.inflate(R.layout.dialog_custom, null)

            val layoutParam : Int = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }

            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParam,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,

                PixelFormat.TRANSLUCENT
            )

            params!!.gravity = Gravity.CENTER
            params!!.x = 0;
            params!!.y = 100;

            mWindowManager!!.addView(rootView, params)
            rootView!!.setOnTouchListener(mViewTouchListener)
            rootView!!.findViewById<View>(R.id.cancel_button).setOnClickListener {
                onDestroy()
            }
        }
    }


    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService", "서비스 동작")
        val phoneNumber = intent?.getStringExtra("phone_number")

        if (intent == null) {
            Log.d("인텐트", "없음")
            return START_STICKY // 서비스가 종료 되었을 때도 다시 자동으로 실행
        }

        if(!Settings.canDrawOverlays(this)) {
            Log.d("오버레이 권한", "없음")
        }else {
            Log.d("오버레이 권한", "있음")
        }

        if (phoneNumber != null) {
            Log.d("폰넘버", phoneNumber)

            // 코루틴 적용
            CoroutineScope(Dispatchers.IO).launch {
                val token = service.getJwtToken()
                val bearerToken = "Bearer " + token.body()?.token.toString()
                GlobalApplication.prefs.setString("Authorization", bearerToken)
                Log.d("토큰", token.body()?.token.toString())
                if(token.isSuccessful) {

                    val phone = phoneNumber.replace("-", "")
                    val response = service.getCustomerInfo(phone)
//                    val response = service.getCustomerInfo("01062491684")
                    val codeData = service.getCodeData()

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body()?.code == "SUCCESS") {
                            val customerCategory = response.body()?.data?.type
                            createView(true, customerCategory)
                            Log.d("통신", "성공")

                            val typeOne  = codeData.body()?.type1?.getAsJsonObject(response.body()?.data?.type1)?.get("value")?.asString
                            val typeTwo = codeData.body()?.type1?.getAsJsonObject(response.body()?.data?.type1)?.getAsJsonObject("type2")?.get(response.body()?.data?.type2)?.asString
                            val typeThree = codeData.body()?.type3?.get(response.body()?.data?.type3)?.asString
                            Log.d("치환1", typeOne.toString())
                            Log.d("치환2", typeTwo.toString())
                            Log.d("치환3", typeThree.toString())

                            when (customerCategory) {
                                "SALE" -> {
                                    // 최상단 데이터
                                    nameTxt = rootView?.findViewById<View>(R.id.nameTxt) as TextView
                                    nameTxt?.text = "최상단 데이터(미정) -> $customerCategory(물건)"

                                    // 핸드폰 번호
                                    phoneTxt = rootView?.findViewById<View>(R.id.phoneTxt) as TextView
                                    phoneTxt?.text = phoneNumber

                                    // 유형
                                    category = rootView?.findViewById<View>(R.id.type) as TextView
                                    category?.text = codeData.body()?.payType?.get(response.body()?.data?.payType)?.asString

                                    // 소유주(유형 옆)
                                    rightOwner = rootView?.findViewById<View>(R.id.owner_id) as TextView
                                    rightOwner?.text = response.body()?.data?.name

                                    // 상태
                                    statusType = rootView?.findViewById<View>(R.id.status_type) as TextView
                                    val statusCode : String = response.body()?.data?.status.toString()
                                    statusType?.text = codeData.body()?.status?.get(statusCode)?.asString

                                    when(statusCode) { // 배경색 변경 코드
                                        "1" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FFBB86FC"))
                                        }

                                        "2" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200EE"))
                                        }

                                        "3" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF3700B3"))
                                        }

                                        "4" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF03DAC5"))
                                        }

                                        "5" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF018786"))
                                        }

                                        "6" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#1133DF"))
                                        }
                                    }

                                    // 가격
                                    price = rootView?.findViewById<View>(R.id.price) as TextView
                                    price?.text = response.body()?.data?.price

                                    // 주소
                                    address = rootView?.findViewById<View>(R.id.address) as TextView
                                    address?.text = response.body()?.data?.address

                                    // 물건 번호
                                    itemNumber = rootView?.findViewById<View>(R.id.item_number) as TextView
                                    itemNumber?.text = response.body()?.data?.saleUuid

                                    // 소유주 이름
                                    owner = rootView?.findViewById<View>(R.id.owner) as TextView
                                    owner?.text = response.body()?.data?.name

                                    // 노트
                                    note = rootView?.findViewById<View>(R.id.note_detail) as TextView
                                    note?.text = response.body()?.data?.comment

                                    // 특징
                                    specialFeature = rootView?.findViewById<View>(R.id.special_feature) as TextView
                                    specialFeature?.text = "오전 중에 통화 불가능 오전 중에 통화 불가능 오전 중에 통화 불가능 오전 중에 통화 불가능"

                                    changeActivity(phoneNumber)
                                }
                                "CUSTOMER" -> {
                                    // 최상단 데이터
                                    nameTxt = rootView?.findViewById<View>(R.id.nameTxt) as TextView
                                    nameTxt?.text = "최상단 데이터(미정) -> $customerCategory(손님)"

                                    // 핸드폰 번호
                                    phoneTxt = rootView?.findViewById<View>(R.id.phoneTxt) as TextView
                                    phoneTxt?.text = phoneNumber

                                    // 유형
                                    category = rootView?.findViewById<View>(R.id.type) as TextView
                                    category?.text = codeData.body()?.payType?.get(response.body()?.data?.payType)?.asString

                                    // 소유주(유형 옆)
                                    rightOwner = rootView?.findViewById<View>(R.id.owner_id) as TextView
                                    rightOwner?.text = response.body()?.data?.name

                                    // 상태
                                    statusType = rootView?.findViewById<View>(R.id.status_type) as TextView
                                    val statusCode : String = response.body()?.data?.status.toString()
                                    statusType?.text = codeData.body()?.status?.get(statusCode)?.asString

                                    when(statusCode) { // 배경색 변경 코드
                                        "1" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FFBB86FC"))
                                        }

                                        "2" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200EE"))
                                        }

                                        "3" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF3700B3"))
                                        }

                                        "4" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF03DAC5"))
                                        }

                                        "5" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF018786"))
                                        }

                                        "6" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#1133DF"))
                                        }
                                    }
                                    // 가격
                                    price = rootView?.findViewById<View>(R.id.price) as TextView
                                    price?.text = response.body()?.data?.price

                                    // 주소
                                    address = rootView?.findViewById<View>(R.id.address) as TextView
                                    address?.text = response.body()?.data?.address

                                    // 물건 번호
                                    itemNumber = rootView?.findViewById<View>(R.id.item_number) as TextView
                                    itemNumber?.text = response.body()?.data?.saleUuid

                                    // 소유주 이름
                                    owner = rootView?.findViewById<View>(R.id.owner) as TextView
                                    owner?.text = response.body()?.data?.name

                                    // 노트
                                    note = rootView?.findViewById<View>(R.id.note_detail) as TextView
                                    note?.text = response.body()?.data?.comment

                                    // 특징
                                    specialFeature = rootView?.findViewById<View>(R.id.special_feature) as TextView
                                    specialFeature?.text = "오전 중에 통화 불가능 오전 중에 통화 불가능 오전 중에 통화 불가능 오전 중에 통화 불가능"

                                    changeActivity(phoneNumber)
                                }
                                else -> {
                                    // 최상단 데이터
                                    nameTxt = rootView?.findViewById<View>(R.id.nameTxt) as TextView
                                    nameTxt?.text = "최상단 데이터(미정)"

                                    // 핸드폰 번호
                                    phoneTxt = rootView?.findViewById<View>(R.id.phoneTxt) as TextView
                                    phoneTxt?.text = phoneNumber

                                    // 유형
                                    category = rootView?.findViewById<View>(R.id.type) as TextView
                                    category?.text = codeData.body()?.payType?.get(response.body()?.data?.payType)?.asString

                                    // 소유주(유형 옆)
                                    rightOwner = rootView?.findViewById<View>(R.id.owner_id) as TextView
                                    rightOwner?.text = response.body()?.data?.name

                                    // 상태
                                    statusType = rootView?.findViewById<View>(R.id.status_type) as TextView
                                    val statusCode : String = response.body()?.data?.status.toString()
                                    statusType?.text = codeData.body()?.status?.get(statusCode)?.asString

                                    when(statusCode) { // 배경색 변경 코드
                                        "1" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FFBB86FC"))
                                        }

                                        "2" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200EE"))
                                        }

                                        "3" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF3700B3"))
                                        }

                                        "4" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF03DAC5"))
                                        }

                                        "5" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF018786"))
                                        }

                                        "6" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#1133DF"))
                                        }
                                    }
                                    // 가격
                                    price = rootView?.findViewById<View>(R.id.price) as TextView
                                    price?.text = response.body()?.data?.price

                                    // 주소
                                    address = rootView?.findViewById<View>(R.id.address) as TextView
                                    address?.text = response.body()?.data?.address

                                    // 물건 번호
                                    itemNumber = rootView?.findViewById<View>(R.id.item_number) as TextView
                                    itemNumber?.text = response.body()?.data?.saleUuid

                                    // 소유주 이름
                                    owner = rootView?.findViewById<View>(R.id.owner) as TextView
                                    owner?.text = response.body()?.data?.name

                                    // 노트
                                    note = rootView?.findViewById<View>(R.id.note_detail) as TextView
                                    note?.text = response.body()?.data?.comment

                                    // 특징
                                    specialFeature = rootView?.findViewById<View>(R.id.special_feature) as TextView
                                    specialFeature?.text = "오전 중에 통화 불가능 오전 중에 통화 불가능 오전 중에 통화 불가능 오전 중에 통화 불가능"

                                    changeActivity(phoneNumber)
                                }
                            }

                        } else if (response.isSuccessful && response.body()?.code == "NO_DATA" ) {
                            createView(popupFlag, null)
                            phoneTxt = rootView?.findViewById<View>(R.id.phoneTxt) as TextView
                            phoneTxt?.text = phoneNumber


                            Log.d("상태 코드2", response.code().toString())
                            Log.d("응답", response.errorBody()?.string().toString())

//                            testActivity(phoneNumber)
                            registerActivity(phoneNumber)
                        } else {
                            Log.d("3번째", "실패")
                            Log.d("데이터", response.body()!!.toString())
                            Log.d("트루", response.isSuccessful.toString())
                        }
                    }
                }
            }
        }
        return START_STICKY
    }


    private fun registerActivity(phoneNumber: String?) {
        val customerRegister: View = rootView!!.findViewById(R.id.customer_register_button)
        val itemRegister: View = rootView!!.findViewById(R.id.item_register_button)



        customerRegister.setOnClickListener {
            val intent = Intent(this, CustomerRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            onDestroy()
        }

        itemRegister.setOnClickListener {
            val intent = Intent(this@CallingService, ItemRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            onDestroy()
        }
    }

    private fun changeActivity(phoneNumber: String?) {

        val detailView : View = rootView!!.findViewById(R.id.detail_view_button)
        val noteRegister : View = rootView!!.findViewById(R.id.note_register_button)

        detailView.setOnClickListener {
            val intent = Intent(this, DetailViewActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            onDestroy()
        }

        noteRegister.setOnClickListener {
            val intent = Intent(this, NoteRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            onDestroy()
        }

    }


    private fun createNotification() {
        val builder = NotificationCompat.Builder(this, "default")
        builder.setSmallIcon(R.mipmap.budongsan_memory)
        builder.setContentTitle("부동산 기억")
        builder.setContentText("포그라운드 서비스")
        builder.color = Color.WHITE
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent) // 알림 클릭 시 이동

        // 알림 표시
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build()) // id : 정의해야하는 각 알림의 고유한 int값
        val notification = builder.build()
        startForeground(NOTIFICATION_ID, notification)

    }

    @SuppressLint("ClickableViewAccessibility")
    private val mViewTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isMove = false
                mTouchX = event.rawX
                mTouchY = event.rawY
                mViewX = params!!.x
                mViewY = params!!.y
            }
            MotionEvent.ACTION_UP -> if (!isMove) {
                Toast.makeText(
                    applicationContext, "터치됨", Toast.LENGTH_SHORT
                ).show()
            }
            MotionEvent.ACTION_MOVE -> {
                isMove = true
                val x = (event.rawX - mTouchX).toInt()
                val y = (event.rawY - mTouchY!!).toInt()
                val num = 5
                if (x > -num && x < num && y > -num && y < num) {
                    isMove = false
//                    break
                }
                /**
                 * mParams.gravity에 따른 부호 변경
                 *
                 * LEFT : x가 +
                 *
                 * RIGHT : x가 -
                 *
                 * TOP : y가 +
                 *
                 * BOTTOM : y가 -
                 */
                params!!.x = mViewX + x
                params!!.y = mViewY!! + y
                mWindowManager!!.updateViewLayout(rootView, params)
            }
        }
        true
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mWindowManager != null) {
            if (rootView != null) {
                mWindowManager!!.removeView(rootView); // View 초기화
                rootView = null;
            }
            mWindowManager = null;
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}


