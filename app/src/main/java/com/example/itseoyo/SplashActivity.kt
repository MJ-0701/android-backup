package com.example.itseoyo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        setContentView(R.layout.splashscreen)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {

        val permissionsCode = 1
        val permissions = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.RECEIVE_BOOT_COMPLETED)

        // 사용자가 두번이상 권한을 거부한 경우 앱에서 권한을 재요청할수 없음.
        if (ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED

            || ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED

            || ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED

            || ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED

            || ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED

            || ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.RECEIVE_BOOT_COMPLETED
            ) != PackageManager.PERMISSION_GRANTED

        ) {
            ActivityCompat.requestPermissions(
                this@SplashActivity, permissions, permissionsCode
            )
        } else {
            nextActivity()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResult: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult)

        grantResult.forEach {
            Log.d("권한 값 : ", it.toString())
        }

        if (requestCode == 1 && grantResult.isNotEmpty()) {
            if (grantResult[0] == 0 && grantResult[1] == 0) {
                //해당 권한이 승낙된 경우.
                nextActivity()
            } else {
                //해당 권한이 거절된 경우.
                Toast.makeText(
                    applicationContext,
                    "필수 권한을 허용 후 서비스를 이용해주시기 바랍니다.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun nextActivity() {
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}