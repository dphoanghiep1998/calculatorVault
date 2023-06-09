package com.neko.hiepdph.calculatorvault.shake

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.neko.hiepdph.calculatorvault.common.extensions.config
import kotlin.math.sqrt


class ShakeDetector(private val context: Context) : SensorEventListener {
    interface OnShakeListener {
        fun onShake(count: Int)
    }

    private val SHAKE_THRESHOLD_GRAVITY = 2.7f
    private val SHAKE_SLOP_TIME_MS = 500
    private val SHAKE_COUNT_RESET_TIME_MS = 3000

    private var mListener: OnShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount = 0

    fun setOnShakeListener(listener: OnShakeListener?) {
        mListener = listener
    }
    override fun onSensorChanged(event: SensorEvent) {
        if (mListener != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x.div(SensorManager.GRAVITY_EARTH)
            val gY = y.div(SensorManager.GRAVITY_EARTH)
            val gZ = z.div(SensorManager.GRAVITY_EARTH)

            // gForce will be close to 1 when there is no movement.
            val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

            if (gForce > context.config.shakeGravity) {
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0
                }

                mShakeTimestamp = now;
                mShakeCount++;

                mListener?.onShake(mShakeCount);
            }
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //ignore
    }
}