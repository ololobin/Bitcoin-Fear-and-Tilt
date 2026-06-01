package com.example.myapplication.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

object IconManager {

    private const val TAG = "IconManager"

    fun changeAppIcon(context: Context, sentiment: String) {
        val targetAlias = when (sentiment) {
            "extreme_fear" -> "com.example.myapplication.MainActivityExtremeFear"
            "fear" -> "com.example.myapplication.MainActivityFear"
            "neutral" -> "com.example.myapplication.MainActivityNeutral"
            "greed" -> "com.example.myapplication.MainActivityGreed"
            "extreme_greed" -> "com.example.myapplication.MainActivityExtremeGreed"
            else -> "com.example.myapplication.MainActivityNeutral"
        }

        val aliases = listOf(
            "com.example.myapplication.MainActivityNeutral",
            "com.example.myapplication.MainActivityFear",
            "com.example.myapplication.MainActivityExtremeFear",
            "com.example.myapplication.MainActivityGreed",
            "com.example.myapplication.MainActivityExtremeGreed"
        )

        val pm = context.packageManager
        
        val componentName = ComponentName(context, targetAlias)
        try {
            val currentEnabledState = pm.getComponentEnabledSetting(componentName)
            if (currentEnabledState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                Log.d(TAG, "Icon alias $targetAlias is already enabled. Skipping switch.")
                return
            }
        } catch (e: Exception) {
            // State is not set explicitly yet
        }

        Log.d(TAG, "Switching app icon to: $targetAlias")
        for (alias in aliases) {
            val state = if (alias == targetAlias) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            try {
                pm.setComponentEnabledSetting(
                    ComponentName(context, alias),
                    state,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update enabled setting for $alias", e)
            }
        }
    }
}
