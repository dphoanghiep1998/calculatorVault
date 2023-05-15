package com.neko.hiepdph.calculatorvault.changeicon

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager


class AppIconNameChanger {
    private var activity: Activity? = null
    var disableNames: List<String>? = null
    var activeName: String? = null
    var packageName: String? = null

    constructor(builder: Builder) {
        disableNames = builder.disableNames
        activity = builder.activity
        activeName = builder.activeName
        packageName = builder.packageName
    }

    class Builder(val activity: Activity) {
        var disableNames: List<String>? = null
        var activeName: String? = null
        var packageName: String? = null
        fun disableNames(disableNamesl: List<String>?): Builder {
            this.disableNames = disableNamesl
            return this
        }

        fun activeName(activeName: String?): Builder {
            this.activeName = activeName
            return this
        }

        fun packageName(packageName: String?): Builder {
            this.packageName = packageName
            return this
        }

        fun build(): AppIconNameChanger {
            return AppIconNameChanger(this)
        }
    }

    fun setNow() {
        activity!!.packageManager.setComponentEnabledSetting(
            ComponentName(packageName!!, activeName!!),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        for (i in disableNames!!.indices) {
            try {
                activity!!.packageManager.setComponentEnabledSetting(
                    ComponentName(packageName!!, disableNames!![i]),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}