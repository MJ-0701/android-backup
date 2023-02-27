package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity


class IncomingCallBroadCastReceiver : BroadcastReceiver() {


    var memberKey : String? = null
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Receiver", "동작")

        val serviceIntent = Intent(context, CallingService::class.java)


        val telephonyManager =
            context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager

        val callNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        val memberIdx = GlobalApplication.prefs.getString("MEMBER_IDX", "")

        if (callNumber == null) {
            Log.d("call_state", "NULL")
        } else {
//            serviceIntent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            serviceIntent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
            if (TelephonyManager.EXTRA_STATE_OFFHOOK == state) { // 통화중
                Toast.makeText(context, "call Active", Toast.LENGTH_SHORT).show()
            } else if (TelephonyManager.EXTRA_STATE_IDLE == state) { // 종료
                Toast.makeText(context, "No call", Toast.LENGTH_SHORT).show()
            } else if (TelephonyManager.EXTRA_STATE_RINGING == state) {
                Log.d("리시버", "전화 벨 울리는 중")
                Log.d("Call_Number :", callNumber)
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                val phoneNumber = PhoneNumberUtils.formatNumber(incomingNumber)
//                val serviceIntent = Intent(context, CallingService::class.java)
                serviceIntent.putExtra("phone_number", phoneNumber)
                serviceIntent.putExtra("MEMBER_IDX", memberIdx)
                Log.d("Phone_Number :", phoneNumber)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Log.d("서비스 시작", "전1")
                    Log.d("브로드캐스트 컨텍스트", context.toString())
                    context.startForegroundService(serviceIntent)
                } else {
                    Log.d("서비스 시작", "전2")
                    Log.d("브로드캐스트 컨텍스트", context.toString())
                    context.startService(serviceIntent)

                }

            }
        }


    }
}