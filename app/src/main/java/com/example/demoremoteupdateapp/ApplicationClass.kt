package com.example.demoremoteupdateapp

import android.app.Application
import com.example.demoremoteupdateapp.updateChecker.UpdateHelper
import com.google.firebase.remoteconfig.FirebaseRemoteConfig


class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val defaultValue: MutableMap<String, Any> = HashMap()
        defaultValue[UpdateHelper.KEY_UPDATE_ENABLE] = false
        defaultValue[UpdateHelper.KEY_UPDATE_VERSION] = "1.0"
        defaultValue[UpdateHelper.KEY_UPDATE_URL] =
            "https://drive.google.com/file/d/1BlKhCoA3vkh8tJAaScV5wyQ2MxoKoWim/view?usp=share_link"

        remoteConfig.setDefaultsAsync(defaultValue)
        remoteConfig.fetch(3).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                remoteConfig.fetchAndActivate()
            }
        }
    }
}