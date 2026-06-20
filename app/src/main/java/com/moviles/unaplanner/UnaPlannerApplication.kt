package com.moviles.unaplanner

import androidx.multidex.MultiDexApplication
import com.moviles.unaplanner.data.AppContainer
import com.moviles.unaplanner.notifications.NotificationHelper

class UnaPlannerApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
        // Channel created here to exist even if the app is closed
        // when a Firebase push arrives
        NotificationHelper.createChannel(this)
    }
}
