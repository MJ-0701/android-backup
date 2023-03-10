package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.os.Parcelable
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

    private var webView: WebView? = null
    private var mWebViewInterface: WebViewInterFace? = null
    private var mProgressBar : ProgressBar? = null
    var uri: Uri? = null
    var imageView: ImageView? = null

    var mWebViewImageUpload: ValueCallback<Array<Uri>>? = null
    var filePathCallbackNormal: ValueCallback<Uri>? = null
    private var cameraImageUri: Uri? = null

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        drawActivity()
//        setContentView(R.layout.test_activity)
//
//        val receiver : TestReceiver = TestReceiver()
//        val filter : IntentFilter = IntentFilter()
//
//        filter.addAction(Intent.ACTION_POWER_CONNECTED)
//        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
//
//        val customerRegister: View = findViewById(R.id.customer_register_button)
//        val itemRegister: View = findViewById(R.id.item_register_button)
//
//        customerRegister.setOnClickListener {
//            Log.d("??????", "??????")
//            val intent = Intent(this, TestReceiver::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            this.registerReceiver(receiver, filter)
//            intent.putExtra("test", "??????")
//            sendBroadcast(intent)
//        }
//
//        itemRegister.setOnClickListener {
//            Log.d("??????", "??????")
//            val intent = Intent(this, TestReceiver::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            this.registerReceiver(receiver, filter)
//            intent.putExtra("test", "??????")
//            sendBroadcast(intent)
//        }

    }



    @SuppressLint("SetJavaScriptEnabled")
    private fun drawActivity() {
//        createNotification()
        setContentView(R.layout.activity_webview)
        webView = findViewById<View>(R.id.webView) as WebView
        mWebViewInterface = WebViewInterFace(this@MainActivity)
        getHashKey()

        webView!!.apply {
            webViewClient = WebViewClientClass() // new WebViewClient(); // ?????? ?????????

            // ???????????? ?????? ????????? ??? ??????????????? ?????? ChromeClient ??????
            // ???????????? ????????? ???????????? && ?????????????????? ??????
            // webChromeClient = WebChromeClient()

            // ???????????? ????????? ???????????? ??????
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

                // ?????? ALERT ??? ?????????
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
                    // Callback ????????? (??????!)
                    if (mWebViewImageUpload != null) {
                        mWebViewImageUpload!!.onReceiveValue(null)
                        mWebViewImageUpload = null
                    }
                    mWebViewImageUpload = filePathCallback

                    val isCapture = fileChooserParams!!.isCaptureEnabled
                    Log.d("??????", isCapture.toString())
                    runCamera(isCapture)
                    return true
                }
            }

            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true) // ??????????????? ????????????
            settings.javaScriptCanOpenWindowsAutomatically = true // ?????????????????? ???????????????(?????????) ????????????
            settings.loadWithOverviewMode = true // ???????????? ????????????
            settings.useWideViewPort = true // ?????? ????????? ????????? ????????????
            settings.setSupportZoom(true) // ?????? ??? ????????????
//            settings.builtInZoomControls = true // ?????? ?????? ?????? ????????????

            // Enable and setup web view cache
            settings.cacheMode = WebSettings.LOAD_NO_CACHE // ???????????? ?????? ????????????
            settings.domStorageEnabled = true // ??????????????? ????????????
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

        // File ????????? URI ??? ?????????.
        cameraImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val strPa = applicationContext.packageName
            FileProvider.getUriForFile(this, applicationContext.packageName + ".fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        if (isCapture) { // ???????????? ?????????, ????????? ?????? ????????? ?????? ???..
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            pickIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val pickTitle = "?????? ????????? ????????? ???????????????."
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)

            // ????????? intent ???????????????..
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(intentCamera))
            launcher.launch(chooserIntent)

        } else { // ???????????? ??????
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            pickIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val pickTitle = "?????? ????????? ????????? ???????????????."
            val chooserIntent = Intent.createChooser(pickIntent, pickTitle)
            launcher.launch(chooserIntent)
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("??????", result.toString())
        if (result.resultCode == RESULT_OK) { // ????????? ????????? ?????? ??????
            val intent = result.data
            Log.d("????????? ?????????", intent.toString())

            mWebViewImageUpload = if(intent == null){ //?????? ????????? ????????? ????????? ??????
                Log.d("?????? ??????", "?????????")
                val results = cameraImageUri!!.apply { Log.d("?????????", this.toString()) }
                mWebViewImageUpload!!.onReceiveValue(arrayOf(results))
                null
            } else{ //?????? ?????? ?????? ????????? ????????? ??????
                Log.d("?????? ??????", "?????????")
                val results = intent.data!!.apply { Log.d("????????? ??????", this.toString()) }
                mWebViewImageUpload!!.onReceiveValue(arrayOf(results))
                null
            }
        }else{ //?????? ??? ?????? ?????????
            Log.d("????????? ?????????", "??? ?????????")
            Log.d("?????? ???", result.toString())

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

        // ????????? ??????
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

//            if (Uri.parse(url).host == "13.209.222.253") {
//                // This is my web site, so do not override; let my WebView load the page -> ?????? ??? ????????? ??? ?????? ????????? ?????? -> return false ??? ????????? ?????? ?????????????????? ?????? ?????????.
//                return false
//            }
//            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs -> ???????????? ?????? ????????? ?????? ?????????????????? ??????.
//            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
//                startActivity(this)
//            }
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

//        @SuppressLint("WebViewClientOnReceivedSslError")
//        override fun onReceivedSslError(
//            view: WebView?,
//            handler: SslErrorHandler?,
//            error: SslError?
//        ) {
//            val builder : AlertDialog.Builder = android.app.AlertDialog.Builder(this@MainActivity)
//            var message = "SSL Certificate error"
//            when(error?.primaryError) {
//                SslError.SSL_UNTRUSTED -> message = "???????????? ????????? ??? ??????"
//                SslError.SSL_EXPIRED -> message = "?????? ??????"
//                SslError.SSL_IDMISMATCH -> message = "?????? ??????"
//                SslError.SSL_NOTYETVALID -> message = "????????? ?????? ?????? ??????"
//            }
//            message += "?????????????????????????"
//            builder.setTitle("SSL Certificate Error")
//            builder.setMessage(message)
//            builder.setPositiveButton("continue", DialogInterface.OnClickListener { _, _ -> handler?.proceed() })
//            builder.setNegativeButton("cancel", DialogInterface.OnClickListener { _, _ -> handler?.cancel() })
//            val dialog : android.app.AlertDialog? = builder.create()
//            dialog?.show()
//        }
    }


    private fun checkPermission() {

        if (!Settings.canDrawOverlays(this)) { // ???????????? ?????? ??????
            showOverlayDialog()
//            startService()
        }else {
//            startService()
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
        dialog.show(supportFragmentManager, "???????????? ??????")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification() {
        val builder = NotificationCompat.Builder(this, "default")
        builder.setSmallIcon(R.mipmap.budongsan_memory)
        builder.setContentTitle("????????? ??????")
        builder.setContentText("??????????????? ?????????")
        builder.color = Color.WHITE
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent) // ?????? ?????? ??? ??????

        // ?????? ??????
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "?????? ??????",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        val NOTIFICATION_ID = 1234
        notificationManager.notify(NOTIFICATION_ID, builder.build()) // id : ?????????????????? ??? ????????? ????????? int???


        val service = Intent(applicationContext, CallingService::class.java)
        service.setPackage("package:$packageName")
        service.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            this.startForegroundService(service)
        }else{
            this.startService(service)
        }


    }



}