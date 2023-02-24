package com.example.itseoyo.`object`

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context : Context) {

    /*
    * MODE_PRIVATE : 생성한 Application에서만 사용 가능하다.
    * MODE_WORLD_READABLE : 외부 App에서 사용 가능, 하지만 읽기만 가능하다
    * MODE_WORLD_WRITEABLE : 외부 App에서 사용 가능, 읽기/쓰기 모두 가능하다
    * */

    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }
}