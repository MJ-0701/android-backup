package com.example.itseoyo

import android.app.Activity
import android.webkit.JavascriptInterface
import android.widget.Toast


class WebViewInterFace constructor(private val mContext : Activity) {


//    /**
//     * 생성자.
//     * @param activity : context
//     * @param view : 적용될 웹뷰
//     */

//    private var mAppView: WebView? = null
//    private var mContext: Activity? = null
//
//    init {
//        this.mAppView = mAppView;
//        this.mContext = mContext;
//    }

    /**
     * 안드로이드 토스트를 출력한다. Time Long.
     * @param message : 메시지
     */
    @JavascriptInterface
    fun toastLong(message: String?) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
    }

    /**
     * 안드로이드 토스트를 출력한다. Time Short.
     * @param message : 메시지
     */
    @JavascriptInterface
    fun toastShort(message: String?) { // Show toast for a short time
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}