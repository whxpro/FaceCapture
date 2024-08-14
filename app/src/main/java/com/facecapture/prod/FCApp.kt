package com.facecapture.prod

import android.app.Application

class FCApp : Application() {
    companion object {
        @JvmStatic
        private var ins: FCApp? = null

        @JvmStatic
        fun getApp(): FCApp {
            return ins!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        ins = this
    }
}