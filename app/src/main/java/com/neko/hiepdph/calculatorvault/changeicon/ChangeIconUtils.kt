package com.neko.hiepdph.calculatorvault.changeicon

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.widget.Toast


object ChangeIconUtils {
     fun changeIcon(activity: Activity) {
        // enable old icon
        val manager: PackageManager = activity.packageManager
        manager.setComponentEnabledSetting(
            ComponentName(
                activity, "com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault"
            ), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )

        // disable new icon
        manager.setComponentEnabledSetting(
            ComponentName(
                activity, "com.neko.hiepdph.calculatorvault.ui.activities.ActivityVaultAlias"
            ), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        Toast.makeText(
                activity, "Enable Old Icon", Toast.LENGTH_LONG
            ).show()
    }

     fun newIcon(activity: Activity) {
        // disable old icon
        val manager: PackageManager = activity.packageManager
        manager.setComponentEnabledSetting(
            ComponentName(
                activity, "com.neko.hiepdph.calculatorvault.ui.activities.ActivityCalculator"
            ), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )

        // enable new icon
        manager.setComponentEnabledSetting(
            ComponentName(
                activity, "com.neko.hiepdph.calculatorvault.ui.activities.ActivityVaultAlias"
            ), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        Toast.makeText(
                activity, "Enable New Icon", Toast.LENGTH_LONG
            ).show()
    }
}