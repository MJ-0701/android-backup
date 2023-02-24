package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import com.example.itseoyo.`object`.PreferenceUtil

class GlobalApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var obj: GlobalApplication? = null

        @SuppressLint("StaticFieldLeak")
        private val currentActivity: Activity? = null

        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceUtil(applicationContext)
        obj = this
    }
}