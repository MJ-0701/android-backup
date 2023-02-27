package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider

import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var dialog: OverlayDialog

    private val url = "http://13.209.222.253/member/login"
//    private val url = "http://192.168.172.158:8080/index/test"

    private var webView: WebView? = null
    private var mWebViewInterface: WebViewInterFace? = null
    private var mProgressBar : ProgressBar? = null
    var uri: Uri? = null
    var imageView: ImageView? = null

    var mWebViewImageUpload: ValueCallback<Array<Uri>>? = null
    var filePathCallbackNormal: ValueCallback<Uri>? = null
    private var cameraImageUri: Uri? = null

    private val handler = Handler()

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



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        drawActivity()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    private fun drawActivity() {
        setContentView(R.layout.activity_webview)
        webView = findViewById<View>(R.id.webView) as WebView
        mWebViewInterface = WebViewInterFace(this@MainActivity)
        getHashKey()

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
                        webViewClient = WebViewClient()
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

                // 웹뷰 ALERT 창 컨트롤
                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    return super.onJsAlert(view, url, message, result)
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    // Callback 초기화 (중요!)
                    if (mWebViewImageUpload != null) {
                        mWebViewImageUpload!!.onReceiveValue(null)
                        mWebViewImageUpload = null
                    }
                    mWebViewImageUpload = filePathCallback

                    val isCapture = fileChooserParams!!.isCaptureEnabled
                    Log.d("선택", isCapture.toString())
                    runCamera(isCapture)
                    return true
                }
            }

            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true) // 새창띄우기 허용여부
            settings.javaScriptCanOpenWindowsAutomatically = true // 자바스크립트 새창띄우기(멀티뷰) 허용여부
            settings.loadWithOverviewMode = true // 메타태그 허용여부
            settings.useWideViewPort = true // 화면 사이즈 맞추기 허용여부
            settings.setSupportZoom(true) // 화면 줌 허용여부
//            settings.builtInZoomControls = true // 화면 확대 축소 허용여부

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

    @SuppressLint("IntentReset", "SimpleDateFormat")
    private fun runCamera(isCapture: Boolean) {

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val path = filesDir
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_" + timeStamp + "_"
        val file = File(path, "$imageFileName.png")

        // File 객체의 URI 를 얻는다.
        cameraImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val strPa = applicationContext.packageName
            FileProvider.getUriForFile(this, applicationContext.packageName + ".fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        if (isCapture) { // 선택팝업 카메라, 갤러리 둘다 띄우고 싶을 때..
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            pickIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val pickTitle = "사진 가져올 방법을 선택하세요."
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)

            // 카메라 intent 포함시키기..
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(intentCamera))
            launcher.launch(chooserIntent)

        } else { // 갤러리만 실행
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            pickIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val pickTitle = "사진 가져올 방법을 선택하세요."
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)
            launcher.launch(chooserIntent)
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("런쳐", result.toString())
        if (result.resultCode == RESULT_OK) { // 이미지 데이터 콜백 성공
            val intent = result.data
            Log.d("이미지 데이터", intent.toString())

            mWebViewImageUpload = if(intent == null){ //바로 사진을 찍어서 올리는 경우
                Log.d("사진 촬영", "카메라")
                val results = cameraImageUri!!.apply { Log.d("카메라", this.toString()) }
                mWebViewImageUpload!!.onReceiveValue(arrayOf(results))
                null
            } else{ //사진 앱을 통해 사진을 가져온 경우
                Log.d("사진 촬영", "갤러리")
                val results = intent.data!!.apply { Log.d("갤러리 파일", this.toString()) }
                mWebViewImageUpload!!.onReceiveValue(arrayOf(results))
                null
            }
        }else{ //취소 한 경우 초기화
            Log.d("이미지 데이터", "노 데이터")
            Log.d("결과 값", result.toString())

            if (mWebViewImageUpload != null) {
                mWebViewImageUpload!!.onReceiveValue(null)
                mWebViewImageUpload = null
            }

            if (filePathCallbackNormal != null) {
                filePathCallbackNormal!!.onReceiveValue(null)
                filePathCallbackNormal = null
            }
        }
    }


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

        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)

//            Log.d("리소스", url.toString())

            if(url.equals("http://13.209.222.253:8000/api/tokenRefresh")) {
                Log.d("로그인", "확인")

                webView!!.evaluateJavascript("window.NativeInterface.getMemberIdx()") {it ->
                    Log.d("자바스크립트1", it)
                    GlobalApplication.prefs.setString("MEMBER_IDX", it)

                    val broadCastIntent = Intent(this@MainActivity, IncomingCallBroadCastReceiver::class.java)
                    broadCastIntent.putExtra("MEMBER_IDX", it)
                    sendBroadcast(broadCastIntent)
                }

//                webView!!.evaluateJavascript("window.dispatchEvent(getMemberIdx)") {it ->
//                    Log.d("자바스크립트2", it)
//                }
//
//                webView!!.evaluateJavascript("javascript:getMemberIdx()") {it ->
//                    Log.d("자바스크립트3 : ", it)
//                }
            }
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
                SslError.SSL_UNTRUSTED -> message = "인증서를 신뢰할 수 없음"
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

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

//            webView!!.evaluateJavascript("window.NativeInterface.getHello()") {it ->
//                Log.d("자바스크립트1 : ", it)
//            }
//
//            webView!!.evaluateJavascript("window.dispatchEvent(getHello)") {it ->
//                Log.d("자바스크립트2 : ", it)
//            }
//
//            webView!!.evaluateJavascript("javascript:getHello()") {it ->
//                Log.d("자바스크립트3 : ", it)
//            }

            val jwtToken =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2Nzc0Nzg4NzcsImV4cCI6MTY3NzczODA3N30.NMiKGfhy5xJyUgicuIHFSKsqGQ4jPL0eaD84GUNiMWA"
            val payload = jwtToken.split(".")[1]

            val decodedBytes: ByteArray = Base64.getDecoder().decode(payload)
            val decodedString = String(decodedBytes)
            Log.d("jwt1: ", decodedString)

            val decoder = Base64.getUrlDecoder()
            Log.d("jwt2: ", String(decoder.decode(payload)))

//            webView!!.evaluateJavascript("window.NativeInterface.getMemberIdx()") {it ->
//                Log.d("자바스크립트1", it)
//            }
//
//            webView!!.evaluateJavascript("window.dispatchEvent(getMemberIdx)") {it ->
//                Log.d("자바스크립트1", it)
//            }
//
//            webView!!.evaluateJavascript("javascript:getMemberIdx()") {it ->
//                Log.d("자바스크립트3 : ", it)
//            }

        }
    }

    private fun writeLog(log: String) {
        handler.post {
            val logText = null
            logText?.append(log)
        }
    }


    private fun checkPermission() {

        if (!Settings.canDrawOverlays(this)) { // 오버레이 권한 체크
            showOverlayDialog()
            startService()
        }else {
            startService()
        }



    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, CallingService::class.java))
        } else {
            startService(Intent(this, CallingService::class.java))
        }
    }

    private fun showOverlayDialog() {
        val dialog = OverlayDialog()
        dialog.show(supportFragmentManager, "오버레이 설정")
    }
}