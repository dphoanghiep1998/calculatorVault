package com.neko.hiepdph.calculatorvault.hideapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager.ACTION_SECRET_CODE
import android.util.Log
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault


class HideAppReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG", "onReceive123: ")


    }
}