package com.example.itseoyo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.itseoyo.`object`.RetrofitObject
import com.example.itseoyo.retrofitinterface.CustomerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody


class MainActivity : AppCompatActivity() {

    private lateinit var dialog: CustomDialog

    private val url = "http://13.209.222.253/member/login"

    private var webView: WebView? = null
    private var mWebViewInterface: WebViewInterFace? = null
    var uri: Uri? = null
    var imageView: ImageView? = null

    var button: Button? = null
    val service: CustomerService =
        RetrofitObject.getRetrofitInstance().create(CustomerService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        setContentView(R.layout.activity_main)
//        drawActivity()


        button = findViewById(R.id.button2)
        val phoneNumber = intent.getStringExtra("phoneNumber")


        button!!.setOnClickListener {

            Log.d("폰넘버", phoneNumber.toString())


            val itemBody = "물건 번호"
            val nameBody = "채명정"
            val phoneBody = phoneNumber!!
            val contentsBody = "테스트"

            val requestMap: HashMap<String, String> = HashMap()
            requestMap["itemNumber"] = itemBody
            requestMap["userName"] = nameBody
            requestMap["phoneNumber"] = phoneBody
            requestMap["contents"] = contentsBody

            Log.d("dto : ", requestMap.toString())

            CoroutineScope(Dispatchers.IO).launch {
                service.saveCustomerInfo(requestMap)
            }
        }
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
        mWebViewInterface = WebViewInterFace(this@MainActivity)
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
                val newWebView = WebView(this@MainActivity)
                val webSettings = newWebView.settings
                webSettings.javaScriptEnabled = true
                val dialog = Dialog(this@MainActivity)
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

//        webView!!.webViewClient = WebViewClientClass()
//        webView!!.settings.loadWithOverviewMode = true
//        webView!!.settings.useWideViewPort = true


    }

    private inner class WebViewClientClass : WebViewClient() {

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (Uri.parse(url).host.toString() == "13.209.222.253") {
                // This is my web site, so do not override; let my WebView load the page
                Log.d("허용", "안함")
                Log.d("유알엘", Uri.parse(url).host.toString())
                return false
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                Log.d("허용", "함")
                Log.d("유알엘", Uri.parse(url).host.toString())
                startActivity(this)
            }
            return true
        }
    }


    private fun checkPermission() {

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE
                ), 1
            )
        }
        if (!Settings.canDrawOverlays(this)) { // 오버레이 권한 체크
            showDialog()
            Log.d("DrawOverlays1", "UnChecked")
        }else {
            Log.d("DrawOverlays2", "Checked")
        }
    }

    private fun showDialog() {
        val dialog = CustomDialog()
        dialog.show(supportFragmentManager, "권한 설정")
    }

}