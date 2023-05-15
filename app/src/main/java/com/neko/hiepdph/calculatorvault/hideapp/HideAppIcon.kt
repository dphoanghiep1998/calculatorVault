package com.neko.hiepdph.calculatorvault.hideapp

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager


class HideAppIcon {
    private var activity: Activity? = null
    var activeName: String? = null
    var packageName: String? = null

    constructor(builder: Builder) {
        activity = builder.activity
        activeName = builder.activeName
        packageName = builder.packageName
    }

    class Builder(val activity: Activity) {
        var activeName: String? = null
        var packageName: String? = null

        fun activeName(activeName: String?): Builder {
            this.activeName = activeName
            return this
        }

        fun packageName(packageName: String?): Builder {
            this.packageName = packageName
            return this
        }

        fun build(): HideAppIcon {
            return HideAppIcon(this)
        }
    }

    fun showIcon() {
        activity!!.packageManager.setComponentEnabledSetting(
            ComponentName(packageName!!, activeName!!),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun hideIcon(){
        activity!!.packageManager.setComponentEnabledSetting(
            ComponentName(packageName!!, activeName!!),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}