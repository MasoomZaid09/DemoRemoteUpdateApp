package com.example.demoremoteupdateapp.updateChecker

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import android.text.TextUtils
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class UpdateHelper(var click : OnUpdateCheckListener){

    companion object{
        var KEY_UPDATE_ENABLE = "isUpdate"
        var KEY_UPDATE_VERSION = "version"
        var KEY_UPDATE_URL = "update_url"
    }

    fun checkForUpdates(context: Context) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        Log.d("Hello",remoteConfig.getBoolean(KEY_UPDATE_ENABLE).toString())
        if (remoteConfig.getBoolean(KEY_UPDATE_ENABLE)) {
            val currentVersion = remoteConfig.getString(KEY_UPDATE_VERSION)
            val appVersion = getAppVersion(context)
            val updateURL = remoteConfig.getString(KEY_UPDATE_URL)
            if (!TextUtils.equals(currentVersion, appVersion)) {
                click.onUpdateCheckListener(updateURL)
            }
        }
    }

    private fun getAppVersion(context: Context): String {
        var result = ""
        try {
            result = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            result = result.replace("[a-zA-Z] |-".toRegex(), "")
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
        return result
    }
}