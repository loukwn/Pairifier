package com.loukwn.pairifier.util

import android.util.Log

object Logger {
    fun d(message: String) = Log.d("Pairifier", message)
    fun e(message: String, t: Throwable?) = Log.e("Pairifier", "$message: ${t?.message ?: "null"}")
}