package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.itseoyo.`object`.RetrofitObject
import com.example.itseoyo.retrofitinterface.PhoneInfoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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


    var phoneTxt: TextView? = null
//    var nameTxt: TextView? = null
    var itemText: TextView? = null
    var contentTxt: TextView? = null
    var nameTxt : TextView? = null

    private var popupFlag : Boolean = false

    private val service = RetrofitObject.getRetrofitInstance().create(PhoneInfoService::class.java)

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
    }

    @SuppressLint("InflateParams")
    private fun createView(popupFlag: Boolean) {
        val mInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        if(popupFlag) {
            rootView = mInflater.inflate(R.layout.scroll_view, null)
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService", "서비스 동작")
        val phoneNumber = intent?.getStringExtra("phone_number")

        if (intent == null) {
            Log.d("인텐트", "없음")
            return START_STICKY // 서비스가 종료 되었을 때도 다시 자동으로 실행
        } else {
//            createView()
            Log.d("인텐트", "있음")
        }

        if (phoneNumber != null) {
            Log.d("폰넘버", phoneNumber)

            // 코루틴 적용
            CoroutineScope(Dispatchers.IO).launch {
//                val response = service.getCustomerInfoByPhone(phoneNumber)
                val response = service.getCustomerInfoByPhone("01012341234")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        createView(true)
                        Log.d("통신", "성공")
                        Log.d("데이터", response.body()!!.toString())
                        Log.d("플래그 값 :", popupFlag.toString())
                        phoneTxt = rootView?.findViewById<View>(R.id.phoneTxt) as TextView
                        nameTxt = rootView?.findViewById<View>(R.id.nameTxt) as TextView

                        phoneTxt?.text = response.body()!!.phoneNumber
                        nameTxt?.text = response.body()!!.userName

                    } else if (response.isSuccessful.not()) {
                        createView(popupFlag)
                        Log.d("플래그 값 :", popupFlag.toString())
                        phoneTxt = rootView?.findViewById<View>(R.id.phoneTxt) as TextView
//                        nameTxt = rootView?.findViewById<View>(R.id.nameTxt) as TextView
                        itemText = rootView?.findViewById<View>(R.id.itemTxt) as TextView
                        contentTxt = rootView?.findViewById<View>(R.id.contentTxt) as TextView
                        if (response.code().toString() == "999") {
                            phoneTxt?.text = phoneNumber
                            Log.d("상태 코드1", response.code().toString())
                            Log.d("응답", response.errorBody()?.string().toString())
                            changeActivity(phoneNumber)
                        } else {
                            phoneTxt?.text = phoneNumber
                            Log.d("상태 코드2", response.code().toString())
                            Log.d("응답", response.errorBody()?.string().toString())
                            changeActivity(phoneNumber)
                        }
                    } else {
                        Log.d("3번째", "실패")
                    }
                }
            }
        } else {
            Log.d("폰넘버", "안찍힘")
        }

        startForeground(NOTIFICATION_ID, createNotification(this))
//        return super.onStartCommand(intent, flags, startId) // START_NOT_STICKY
        return START_STICKY
    }

    private fun changeActivity(phoneNumber: String?) {
        val customerRegister: View = rootView!!.findViewById(R.id.customer_register_button)
        val itemRegister: View = rootView!!.findViewById(R.id.item_register_button)

        customerRegister.setOnClickListener {
            Log.d("진입", "확인")
            Log.d("신규고객등록 버튼", "확인")
            val intent = Intent(this, CustomerRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            onDestroy()
        }

        itemRegister.setOnClickListener {
            Log.d("진입", "확인")
            Log.d("신규물건등록 버튼", "확인")
            val intent = Intent(this@CallingService, ItemRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            onDestroy()
        }
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification(context: Context): Notification? {

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

        val builder = NotificationCompat.Builder(context, "있어요")
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("있어요")
        builder.setContentText("표기 내용")
        builder.setOngoing(true)
        builder.priority = NotificationCompat.PRIORITY_MIN
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE)
        builder.setContentIntent(notifyPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "있어요"
            val description = "있어요"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("있어요", name, importance)
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
        Log.d("디스트로이", "동작")
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


