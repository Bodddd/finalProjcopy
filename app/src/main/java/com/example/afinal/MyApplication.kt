package com.example.afinal

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyDW4dB2kuQZ_YPI0YCEE6cxrnXKSphpK2I")
        }
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}