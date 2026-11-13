package com.example.util

import android.content.Context
import android.content.SharedPreferences

object LocalStorage {
    private var sharedPreferences: SharedPreferences? = null

    /**
     * Initializes the localStorage preference file.
     */
    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(
                "hazari_local_storage_prefs",
                Context.MODE_PRIVATE
            )
        }
    }

    /**
     * Set a string item in key-value store.
     */
    fun setItem(key: String, value: String) {
        sharedPreferences?.edit()?.putString(key, value)?.apply()
    }

    /**
     * Read a string item from key-value store.
     */
    fun getItem(key: String, defaultValue: String = ""): String {
        return sharedPreferences?.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Remove an item from the store.
     */
    fun removeItem(key: String) {
        sharedPreferences?.edit()?.remove(key)?.apply()
    }

    /**
     * Wipes any persisted browser-refresh mimicking state.
     */
    fun clear() {
        sharedPreferences?.edit()?.clear()?.apply()
    }
}
