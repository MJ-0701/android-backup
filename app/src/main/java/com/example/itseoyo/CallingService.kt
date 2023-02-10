package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.example.itseoyo.dto.ErrorResponse
import com.example.itseoyo.`object`.RetrofitObject
import com.example.itseoyo.retrofitinterface.PhoneInfoService
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.*


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

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onCreate() {
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

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
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

            } else {
                params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                    PixelFormat.TRANSLUCENT
                )

                params!!.gravity = Gravity.CENTER
                mWindowManager!!.addView(rootView, params)
                rootView!!.setOnTouchListener(mViewTouchListener)
                rootView!!.findViewById<View>(R.id.cancel_button).setOnClickListener {
                    Log.d("클릭", "클릭 이벤트")
                    onDestroy()
                }
            }
        }else {
            rootView = mInflater.inflate(R.layout.dialog_custom, null)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
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

            } else {
                params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                    PixelFormat.TRANSLUCENT
                )

                params!!.gravity = Gravity.CENTER
                mWindowManager!!.addView(rootView, params)
                rootView!!.setOnTouchListener(mViewTouchListener)
                rootView!!.findViewById<View>(R.id.cancel_button).setOnClickListener {
                    Log.d("클릭", "클릭 이벤트")
                    onDestroy()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService", "서비스 동작")
        val phoneNumber = intent?.getStringExtra("phone_number")

        if (intent == null) {
            Log.d("인텐트", "없음")
            return START_STICKY // 서비스가 종료 되었을 때도 다시 자동으로 실행
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
//                    val response = service.getCustomerInfo(phone)
                    val response = service.getCustomerInfo("01062491684")
                    val codeData = service.getCodeData()

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body()?.code == "SUCCESS") {
                            val customerCategory = response.body()?.data?.type
                            createView(true, customerCategory)
                            Log.d("통신", "성공")
                            Log.d("데이터", response.body()!!.toString())
                            Log.d("플래그 값 :", popupFlag.toString())
//                            Log.d("코드 데이터 : ", codeData.body().toString())
//                            Log.d("type2 : ", codeData.body()?.type1?.getAsJsonObject("01")?.getAsJsonObject("type2")?.get("0101").toString())

                            val typeOne  = codeData.body()?.type1?.getAsJsonObject(response.body()?.data?.type1)?.get("value").toString()
                            val typeTwo = codeData.body()?.type1?.getAsJsonObject(response.body()?.data?.type1)?.getAsJsonObject("type2")?.get(response.body()?.data?.type2).toString()
                            val typeThree = codeData.body()?.type3?.get(response.body()?.data?.type3).toString()
//                            Log.d("치환1", typeOne)
//                            Log.d("치환2", typeTwo)
//                            Log.d("치환3", typeThree)

                            when (customerCategory) {
                                "SALE" -> {
                                    // 최상단 데이터
                                    nameTxt = rootView?.findViewById<View>(R.id.nameTxt) as TextView
                                    nameTxt?.text = "최상단 데이터(미정) -> SALE(물건)"

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

                                    when(statusCode) {
                                        "1" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200A")) // 배경색 변경 코드
                                        }

                                        "2" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200B")) // 배경색 변경 코드
                                        }

                                        "3" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200C")) // 배경색 변경 코드
                                        }

                                        "4" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200D")) // 배경색 변경 코드
                                        }

                                        "5" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200E")) // 배경색 변경 코드
                                        }

                                        "6" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200F")) // 배경색 변경 코드
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
                                    nameTxt?.text = "최상단 데이터(미정) -> CUSTOMER(손님)"

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

                                    when(statusCode) {
                                        "1" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200A")) // 배경색 변경 코드
                                        }

                                        "2" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200B")) // 배경색 변경 코드
                                        }

                                        "3" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200C")) // 배경색 변경 코드
                                        }

                                        "4" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200D")) // 배경색 변경 코드
                                        }

                                        "5" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200E")) // 배경색 변경 코드
                                        }

                                        "6" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200F")) // 배경색 변경 코드
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

                                    when(statusCode) {
                                        "1" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200A")) // 배경색 변경 코드
                                        }

                                        "2" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200B")) // 배경색 변경 코드
                                        }

                                        "3" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200C")) // 배경색 변경 코드
                                        }

                                        "4" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200D")) // 배경색 변경 코드
                                        }

                                        "5" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200E")) // 배경색 변경 코드
                                        }

                                        "6" -> {
                                            statusType?.setBackgroundColor(Color.parseColor("#FF6200F")) // 배경색 변경 코드
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
                            itemText = rootView?.findViewById<View>(R.id.itemTxt) as TextView
                            contentTxt = rootView?.findViewById<View>(R.id.contentTxt) as TextView
                            phoneTxt?.text = phoneNumber
                            Log.d("상태 코드2", response.code().toString())
                            Log.d("응답", response.errorBody()?.string().toString())
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

        startForeground(NOTIFICATION_ID, createNotification(this))
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


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(context: Context): Notification {

        val notifyIntent = Intent(context, MainActivity::class.java)
        var notifyPendingIntent: PendingIntent? = null
        notifyPendingIntent = if (Build.VERSION.SDK_INT >= 31) {
            PendingIntent.getActivity(
                context,
                0,
                notifyIntent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val builder = NotificationCompat.Builder(context, "부동산 기억")
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("부동산 기억")
        builder.setContentText("표기 내용")
        builder.setOngoing(true)
        builder.priority = NotificationCompat.PRIORITY_MIN
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE)
        builder.setContentIntent(notifyPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "부동산 기억"
            val description = "부동산 기억"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("부동산 기억", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        return builder.build()
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


