package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class NoteRegisterActivity : AppCompatActivity() {

    private val url = "http://13.209.222.253/room/detail_view"

    private var webView: WebView? = null
    private var mWebViewInterface: WebViewInterFace? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("물건등록", "액티비티")
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_item_register)
        drawActivity()
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun drawActivity() {
        setContentView(R.layout.activity_webview)
        webView = findViewById<View>(R.id.webView) as WebView

        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.domStorageEnabled = true
        webView!!.settings.setSupportMultipleWindows(true)
        webView!!.settings.javaScriptCanOpenWindowsAutomatically = true
        webView!!.settings.textZoom = 100
        webView!!.settings.allowFileAccess = false
        webView!!.settings.allowFileAccessFromFileURLs = false
        webView!!.settings.allowUniversalAccessFromFileURLs = false
        mWebViewInterface = WebViewInterFace(this@NoteRegisterActivity)
        webView!!.addJavascriptInterface(mWebViewInterface!!, "Android")

        val intent = intent
        val bundle = intent.extras
        var webViewURL: String? = url

        if (bundle != null) {
            if (bundle.getString("url") != null && !bundle.getString("url")
                    .equals("", ignoreCase = true)
            ) {
                webViewURL = bundle.getString("url")
            }
        }
        webView!!.loadUrl(webViewURL!!)
        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@NoteRegisterActivity)
                val webSettings = newWebView.settings
                webSettings.javaScriptEnabled = true
                val dialog = Dialog(this@NoteRegisterActivity)
                dialog.setContentView(newWebView)
                dialog.show()
                newWebView.webChromeClient = object : WebChromeClient() {
                    override fun onCloseWindow(window: WebView) {
                        dialog.dismiss()
                    }
                }
                (resultMsg.obj as WebView.WebViewTransport).webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }

        webView!!.webViewClient = WebViewClientClass()
        webView!!.settings.loadWithOverviewMode = true
        webView!!.settings.useWideViewPort = false


    }

    private inner class WebViewClientClass : WebViewClient() {

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.d("웹뷰 클라이언트", "웹뷰")
            if (Uri.parse(url).host == "13.209.222.253") {
                // This is my web site, so do not override; let my WebView load the page -> 우리 웹 도메인 일 경우 웹뷰로 호출 -> return false 로 핸드폰 자체 클라이언트로 열지 않는다.
                Log.d("호스팅", "동작")
                return false
            }
            Log.d("호스팅", "동작 안함1")
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs -> 나머지의 경우 핸드폰 자체 클라이언트로 열람.
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                Log.d("호스팅", "동작 안함2")
                startActivity(this)
            }
            return true
        }
    }
}