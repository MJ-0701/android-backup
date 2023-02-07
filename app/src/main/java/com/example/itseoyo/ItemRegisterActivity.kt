package com.example.itseoyo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class ItemRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("물건등록", "액티비티")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_register)
        Log.d("물건등록 뷰", "셋")
    }
}