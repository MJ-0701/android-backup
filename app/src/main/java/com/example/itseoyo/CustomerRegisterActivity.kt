package com.example.itseoyo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class CustomerRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("고객등록", "액티비티")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_register)
        Log.d("고객등록 뷰", "셋")
    }
}