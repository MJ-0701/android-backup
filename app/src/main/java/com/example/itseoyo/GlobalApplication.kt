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


    private var PageIndex: String = ""
    fun getPageIndex(): String? {
        return PageIndex
    }

    fun setPageIndex(str: String) {
        PageIndex = str
    }

    //private static volatile GlobalApplication instance = null;

    //private static volatile GlobalApplication instance = null;


    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceUtil(applicationContext)

        obj = this

    }

    fun getGlobalApplicationContext(): GlobalApplication? {
        return obj
    }


    fun getCurrentActivity(): Activity? {
        return currentActivity
    }


}