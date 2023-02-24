package com.example.itseoyo

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


class TestActivity : AppCompatActivity() {

    var rootView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowManagerTest()
    }

    private fun windowManagerTest() {
        val type =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE

        val mWm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        )

        params.format = PixelFormat.RGBA_8888
        params.gravity = Gravity.TOP or Gravity.CENTER_VERTICAL

        rootView = mInflater.inflate(R.layout.custom_dialog, null)

        mWm.addView(rootView,params)
    }



    override fun onDestroy() {
        super.onDestroy()
    }
}