package com.xunda.mo.staticdata.kotlin

import android.app.Application

class ScreenShotApplication : Application() {

    companion object {
        lateinit var applicationContext: Application
    }

    override fun onCreate() {
        super.onCreate()
        Companion.applicationContext = this
    }
}