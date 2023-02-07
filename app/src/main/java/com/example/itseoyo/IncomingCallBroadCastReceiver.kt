package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class IncomingCallBroadCastReceiver : BroadcastReceiver() {


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Receiver", "동작")

        val telephonyManager =
            context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager

        val callNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (callNumber == null) {
            Log.i("call_state", "NULL")
        } else {
            if (TelephonyManager.EXTRA_STATE_OFFHOOK == state) { // 통화중
                Toast.makeText(context, "call Active", Toast.LENGTH_SHORT).show()
            } else if (TelephonyManager.EXTRA_STATE_IDLE == state) { // 종료
                Toast.makeText(context, "No call", Toast.LENGTH_SHORT).show()
            } else if (TelephonyManager.EXTRA_STATE_RINGING == state) {
                Log.d("리시버", "전화 벨 울리는 중")
                Log.d("Call_Number :", callNumber)
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                val phoneNumber = PhoneNumberUtils.formatNumber(incomingNumber)
                Log.d("Phone_Number :", phoneNumber)
                val serviceIntent = Intent(context, CallingService::class.java)
                serviceIntent.putExtra("phone_number", phoneNumber)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }


    }
}