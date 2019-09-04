package com.epoch.owaste

import android.app.Application

class Owaste: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        lateinit var instance: Owaste
            private set
    }
}