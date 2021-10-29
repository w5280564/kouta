package com.xunda.mo.staticdata.kotlin

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull

/**
 * 利用ContentResolver监听照片数据的变化
 */
fun ContentResolver.registerObserver(uri: Uri,observer: (selfChange: Boolean) -> Unit): ContentObserver {
    val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}