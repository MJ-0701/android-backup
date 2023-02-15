package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var dialog: CustomDialog
    private var webView: WebView? = null
    private var mProgressBar : ProgressBar? = null

    private val url = "http://13.209.222.253/member/login"

    private var mWebViewInterface: WebViewInterFace? = null
    var uri: Uri? = null
    var imageView: ImageView? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        drawActivity()
    }


    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    private fun drawActivity() {
        setContentView(R.layout.activity_webview)
        webView = findViewById<View>(R.id.webView) as WebView
        mWebViewInterface = WebViewInterFace(this@MainActivity)

        webView!!.apply {
            webViewClient = WebViewClientClass() // new WebViewClient(); // 새창 안뜨게

            // 팝업이나 파일 업로드 등 설정해주기 위해 ChromeClient 설정
            // 웹뷰에서 크롬이 실행간으 && 새창띄우기는 안됨
            // webChromeClient = WebChromeClient()

            // 웹뷰에서 팝업창 호출하기 위해
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    val newWebView = WebView(this@MainActivity).apply {
                        webViewClient = WebViewClientClass()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(this@MainActivity).apply {
                        setContentView(newWebView)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT

                        show()
                    }

                    newWebView.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }

                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }

                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    return super.onJsAlert(view, url, message, result)
                }
            }

            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true) // 새창띄우기 허용여부
            settings.javaScriptCanOpenWindowsAutomatically = true // 자바스크립트 새창띄우기(멀티뷰) 허용여부
            settings.loadWithOverviewMode = true // 메타태그 허용여부
            settings.useWideViewPort = true // 화면 사이즈 맞추기 허용여부
            settings.setSupportZoom(true) // 화면 줌 허용여부
            settings.builtInZoomControls = true // 화면 확대 축소 허용여부

            // Enable and setup web view cache
            settings.cacheMode = WebSettings.LOAD_NO_CACHE // 브라우저 캐시 허용여부
            settings.domStorageEnabled = true // 로컬저장소 허용여부
            settings.displayZoomControls = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true // api 26
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings.mediaPlaybackRequiresUserGesture = false
            }

            settings.allowContentAccess = true
            settings.setGeolocationEnabled(true)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                settings.allowUniversalAccessFromFileURLs = true
            }

            settings.allowFileAccess = true

            fitsSystemWindows = true

            addJavascriptInterface(mWebViewInterface!!, "Android")

        }
        webView!!.loadUrl(url)

    }

    // 웹뷰에서 홈페이지를 띄웠을떄 새창이 아닌 기존창에서 실행이 되도록
    private inner class WebViewClientClass : WebViewClient() {
        // 페이지 이동
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

            if (Uri.parse(url).host == "13.209.222.253") {
                // This is my web site, so do not override; let my WebView load the page -> 우리 웹 도메인 일 경우 웹뷰로 호출 -> return false 로 핸드폰 자체 클라이언트로 열지 않는다.
                return false
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs -> 나머지의 경우 핸드폰 자체 클라이언트로 열람.
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                startActivity(this)
            }
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mProgressBar?.visibility = ProgressBar.VISIBLE
            webView?.visibility = View.INVISIBLE
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            mProgressBar?.visibility = ProgressBar.GONE
            webView?.visibility = View.VISIBLE
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            val builder : AlertDialog.Builder = android.app.AlertDialog.Builder(this@MainActivity)
            var message = "SSL Certificate error"
            when(error?.primaryError) {
                SslError.SSL_UNTRUSTED -> message = "인증서 신뢰할 수 없음"
                SslError.SSL_EXPIRED -> message = "기간 만료"
                SslError.SSL_IDMISMATCH -> message = "미스 매치"
                SslError.SSL_NOTYETVALID -> message = "인증서 아직 허가 안됨"
            }
            message += "계속하시겠습니까?"
            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue", DialogInterface.OnClickListener { _, _ -> handler?.proceed() })
            builder.setNegativeButton("cancel", DialogInterface.OnClickListener { _, _ -> handler?.cancel() })
            val dialog : android.app.AlertDialog? = builder.create()
            dialog?.show()
        }
    }


    private fun checkPermission() {

        if (!Settings.canDrawOverlays(this)) { // 오버레이 권한 체크
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = CustomDialog()
        dialog.show(supportFragmentManager, "권한 설정")
    }

}