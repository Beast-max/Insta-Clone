package com.example.instagramapp.utilities

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    fun saveString(context: Context, key: String, value: String?) {
        val prefs: SharedPreferences =
            context.getSharedPreferences("Farmclub", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()

    }
    fun saveInt(context: Context, key: Int, value: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences("Farmclub", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(value, key)
        editor.apply()
    }
    fun getString(context: Context, key: String): String? {
        val prefs: SharedPreferences =
            context.getSharedPreferences("Farmclub", Context.MODE_PRIVATE)
        return prefs.getString(key, "null")
    }
    fun getInt(context: Context, key: String): Int {
        val prefs: SharedPreferences =
            context.getSharedPreferences("Farmclub", Context.MODE_PRIVATE)
        return prefs.getInt(key, -1)
    }

    fun deleteKey(context: Context, key: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences("Farmclub", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove(key)
        editor.apply()
    }
}