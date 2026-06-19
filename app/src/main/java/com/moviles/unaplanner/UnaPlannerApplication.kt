package com.moviles.unaplanner

import androidx.multidex.MultiDexApplication
import com.moviles.unaplanner.data.AppContainer

class UnaPlannerApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
