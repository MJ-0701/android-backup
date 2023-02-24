package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CustomerRegisterActivity : AppCompatActivity() {

    private val url = "http://13.209.222.253/guest/write"

    private var webView: WebView? = null
    private var mProgressBar : ProgressBar? = null
    private var mWebViewInterface: WebViewInterFace? = null

    var cameraPath = ""
    var cameraImageUri : Uri? = null
    var mWebViewImageUpload: ValueCallback<Array<Uri>>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("고객등록", "액티비티")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        drawActivity()

    }

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    private fun drawActivity() {
        setContentView(R.layout.activity_webview)
        webView = findViewById<View>(R.id.webView) as WebView
        mWebViewInterface = WebViewInterFace(this@CustomerRegisterActivity)

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
                    val newWebView = WebView(this@CustomerRegisterActivity).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(this@CustomerRegisterActivity).apply {
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

                // alert 창
                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    result?.cancel()
                    return true
                }

                // HTML input file 컨트롤 함수 -> 이미지 파일 업로드 설정
                @SuppressLint("IntentReset", "QueryPermissionsNeeded")
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    try{
                        mWebViewImageUpload = filePathCallback!!
                        var takePictureIntent : Intent?
                        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        if(takePictureIntent.resolveActivity(packageManager) != null){

                            val photoFile : File = createImageFile()
                            takePictureIntent.putExtra("PhotoPath",cameraPath)

                            Log.d("진입", "확인")

                            cameraImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                FileProvider.getUriForFile(this@CustomerRegisterActivity, applicationContext.packageName + ".fileprovider", photoFile)
                            } else {
                                Uri.fromFile(photoFile)
                            }
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                        }
                        val contentSelectionIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        contentSelectionIntent.type = "image/*"

                        val intentArray: Array<Intent?> = arrayOf(takePictureIntent)

                        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                        chooserIntent.putExtra(Intent.EXTRA_TITLE,"사용할 앱을 선택해주세요.")
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                        launcher.launch(chooserIntent)
                    }
                    catch (e : Exception){
                        Log.e("파일 업로드 처리 에러", e.toString())
                    }
                    return true
                }
            }

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
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

        val intent = intent
        val bundle = intent.extras
        val phoneNumber = intent.getStringExtra("phoneNumber")!!.replace("-", "")
        var webViewURL: String? = "$url?phone=$phoneNumber"

        if (bundle != null) {
            if (bundle.getString("url") != null && !bundle.getString("url")
                    .equals("", ignoreCase = true)
            ) {
                webViewURL = bundle.getString("url")
            }
        }
        webView!!.loadUrl(webViewURL!!)
    }

    // 이미지 파일 업로드
    // 이미지 파일 업로드
    fun createImageFile(): File {
        @SuppressLint("SimpleDateFormat")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_$timeStamp.jpg"
//        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val path = filesDir
        return File(path, imageFileName)
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data

            if(intent == null){ //바로 사진을 찍어서 올리는 경우
                val results = cameraImageUri!!
                Log.d("카메라 실행", results.toString())
                mWebViewImageUpload!!.onReceiveValue(arrayOf(results) )
            }
            else{ //사진 앱을 통해 사진을 가져온 경우
                val results = intent.data!!
                Log.d("갤러리 실행", results.toString())
                mWebViewImageUpload!!.onReceiveValue(arrayOf(results))
            }
        }
        else{ //취소 한 경우 초기화
            mWebViewImageUpload!!.onReceiveValue(null)
            mWebViewImageUpload = null
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

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val phoneNumber = intent.getStringExtra("phoneNumber")!!.replace("-", "")
            Log.d("js 파라미터", phoneNumber)

            webView!!.evaluateJavascript("window.NativeInterface.getAutomaticPhoneNumber('$phoneNumber')") { it ->
                Log.d("SPA1 함수", it)
            }

            webView!!.evaluateJavascript("javascript:getAutomaticPhoneNumber('$phoneNumber')") {it ->
                Log.d("SPA2 함수", it)
            }
        }

        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            val builder : AlertDialog.Builder = android.app.AlertDialog.Builder(this@CustomerRegisterActivity)
            var message = "SSL Certificate error"
            when(error?.primaryError) {
                SslError.SSL_UNTRUSTED -> message = "인증서 신뢰할 수 없음"
                SslError.SSL_EXPIRED -> message = "기간 만료"
                SslError.SSL_IDMISMATCH -> message = "미스 매치"
                SslError.SSL_NOTYETVALID -> message = "인증서 아직 허가 안됨"
            }
            message += "계속 하시겠습니까?"
            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue", DialogInterface.OnClickListener { _, _ -> handler?.proceed() })
            builder.setNegativeButton("cancel", DialogInterface.OnClickListener { _, _ -> handler?.cancel() })
            val dialog : android.app.AlertDialog? = builder.create()
            dialog?.show()
        }
    }

    // 변수 선언
    fun WebView.executeScript(
        variableName: String,
        constructorName: String,
        params: List<Any?> = emptyList(),
        onResult: (value: String) -> Unit = {}
    ) {
        val sb = StringBuilder()

        sb.append("javascript:")
            .append("const ")
            .append(variableName)
            .append(" = ")
            .append("new ")
            .append(constructorName)
            .append("(")
            .append(params.joinToString(", "))
            .append(")")

        evaluateJavascript(sb.toString(), onResult)
    }

    // 함수 호출
    fun WebView.executeScript(
        references: List<String> = emptyList(),
        functionName: String,
        params: List<Any?> = emptyList(),
        onResult: (value: String) -> Unit = {}
    ) {
        val sb = StringBuilder()

        sb.append("javascript:")
            .append(references.joinToString("."))
            .append(".")
            .append(functionName)
            .append("(")
            .append(params.joinToString(", "))
            .append(")")

        evaluateJavascript(sb.toString(), onResult)
    }
}