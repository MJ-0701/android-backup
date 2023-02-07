package com.example.itseoyo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application


class GlobalApplication : Application() {

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
        obj = this
    }

    fun getGlobalApplicationContext(): GlobalApplication? {
        return obj
    }


    fun getCurrentActivity(): Activity? {
        return currentActivity
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var obj: GlobalApplication? = null

        @SuppressLint("StaticFieldLeak")
        private val currentActivity: Activity? = null
    }


}