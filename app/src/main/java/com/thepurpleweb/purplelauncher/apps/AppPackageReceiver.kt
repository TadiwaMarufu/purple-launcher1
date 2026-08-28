package com.thepurpleweb.purplelauncher.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppPackageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_CHANGED,
            Intent.ACTION_PACKAGE_FULLY_REMOVED -> {

                val repository = AppRepository(context)
                repository.invalidateCache()
            }
        }
    }
}
