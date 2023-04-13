package com.neko.hiepdph.calculatorvault.config

import android.content.Context
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY

class MainConfig(val context: Context) {
    companion object {
        fun newInstance(context: Context) = MainConfig(context)
    }

    var isShouldShowHidden: Boolean
        get() = AppSharePreference.getInstance(context).getIsShouldShowHidden(false)
        set(isShouldShowHidden) = AppSharePreference.getInstance(context)
            .setShouldShowHidden(isShouldShowHidden)

    var lockType: Int
        get() = AppSharePreference.getInstance(context).getLockType(LockType.PATTERN)
        set(lockType) = AppSharePreference.getInstance(context).saveLockType(lockType)

    var lockPressType: Int
        get() = AppSharePreference.getInstance(context).getLockPressType(LockPressType.NORMAL)
        set(lockType) = AppSharePreference.getInstance(context).saveLockPressType(lockType)

    var fingerPrintUnlock: Boolean
        get() = AppSharePreference.getInstance(context)
            .getFingerPrintUnlock(FingerPrintUnlock.DISABLE)
        set(fingerPrintUnlock) = AppSharePreference.getInstance(context)
            .saveFingerPrintUnlock(fingerPrintUnlock)

    var fingerPrintLockDisplay: Boolean
        get() = AppSharePreference.getInstance(context)
            .getFingerPrintLockDisplay(FingerPrintLockDisplay.DISABLE)
        set(fingerPrintLockDisplay) = AppSharePreference.getInstance(context)
            .saveFingerPrintLockDisplay(fingerPrintLockDisplay)

    var lockWhenLeavingApp: Boolean
        get() = AppSharePreference.getInstance(context)
            .getLockWhenLeavingApp(LockWhenLeavingApp.DISABLE)
        set(lockWhenLeavingApp) = AppSharePreference.getInstance(context)
            .saveLockWhenLeavingApp(lockWhenLeavingApp)

    var screenOffAction: Int
        get() = AppSharePreference.getInstance(context).getScreenOffAction(ScreenOffAction.NOACTION)
        set(screenOffAction) = AppSharePreference.getInstance(context)
            .saveScreenOffAction(screenOffAction)

    var visiblePattern: Boolean
        get() = AppSharePreference.getInstance(context).getVisiblePattern(VisiblePattern.DISABLE)
        set(visiblePattern) = AppSharePreference.getInstance(context)
            .saveVisiblePattern(visiblePattern)

    var tactileFeedback: Boolean
        get() = AppSharePreference.getInstance(context).getTactileFeedback(TactileFeedback.DISABLE)
        set(tactileFeedback) = AppSharePreference.getInstance(context)
            .saveTactileFeedback(tactileFeedback)

    var secretPin: String
        get() = AppSharePreference.getInstance(context).getSecretPin("1111")
        set(secretPin) = AppSharePreference.getInstance(context).saveSecretPin(secretPin)

    var securityQuestion: String
        get() = AppSharePreference.getInstance(context).getSecurityQuestion(String.EMPTY)
        set(securityQuestion) = AppSharePreference.getInstance(context)
            .setSecurityQuestion(securityQuestion)

    var securityAnswer: String
        get() = AppSharePreference.getInstance(context).getSecurityAnswer(String.EMPTY)
        set(securityAnswer) = AppSharePreference.getInstance(context)
            .setSecurityAnswer(securityAnswer)

    var patternLock:List<Int>
        get() = AppSharePreference.getInstance(context).getPatternLock(arrayListOf())
        set(patternLock) = AppSharePreference.getInstance(context).setPatternLock(patternLock)

    var isShowLock:Boolean
        get() = AppSharePreference.getInstance(context).getShowLock(false)
        set(isShowLock) = AppSharePreference.getInstance(context).setShowLock(isShowLock)


}