package com.neko.hiepdph.calculatorvault.config

import android.content.Context
import android.os.Environment
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.common.utils.buildMinVersionQ
import com.neko.hiepdph.calculatorvault.data.model.ItemMoved
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import java.io.File

class MainConfig(val context: Context) {
    companion object {
        fun newInstance(context: Context) = MainConfig(context)
    }

    var privacyFolder = File(externalStoragePath + "/${Constant.PRIVACY_FOLDER_NAME}")
    var recyclerBinFolder = File(context.filesDir.path + "/${Constant.RECYCLER_BIN_FOLDER_NAME}")
    var intruderFolder = File(context.filesDir.path + "/${Constant.INTRUDER_FOLDER_NAME}")
    var picturePrivacyFolder = File(privacyFolder, Constant.PICTURE_FOLDER_NAME)
    var filePrivacyFolder = File(privacyFolder, Constant.VIDEOS_FOLDER_NAME)
    var audioPrivacyFolder = File(privacyFolder, Constant.AUDIOS_FOLDER_NAME)
    var videoPrivacyFolder = File(privacyFolder, Constant.FILES_FOLDER_NAME)

    var isShouldShowHidden: Boolean
        get() = AppSharePreference.getInstance(context).getIsShouldShowHidden(false)
        set(isShouldShowHidden) = AppSharePreference.getInstance(context)
            .setShouldShowHidden(isShouldShowHidden)

    var lockType: Int
        get() = AppSharePreference.getInstance(context).getLockType(LockType.NONE)
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

    var patternLock: List<Int>
        get() = AppSharePreference.getInstance(context).getPatternLock(arrayListOf())
        set(patternLock) = AppSharePreference.getInstance(context).setPatternLock(patternLock)

    var isShowLock: Boolean
        get() = AppSharePreference.getInstance(context).getShowLock(false)
        set(isShowLock) = AppSharePreference.getInstance(context).setShowLock(isShowLock)

    var slideShowInterval: Int
        get() = AppSharePreference.getInstance(context).getSlideShowInterval(3)
        set(slideShowInterval) = AppSharePreference.getInstance(context)
            .setSlideShowInterval(slideShowInterval)

    var slideShowTransition: Int
        get() = AppSharePreference.getInstance(context)
            .getSlideShowTransition(SlideShowTransition.NATURAL)
        set(slideShowTransition) = AppSharePreference.getInstance(context)
            .setSlideShowTransition(slideShowTransition)

    var moveToRecyclerBin: Boolean
        get() = AppSharePreference.getInstance(context).getMoveToRecyclerBin(RecyclerBin.ENABLE)
        set(moveToRecyclerBin) = AppSharePreference.getInstance(context)
            .setMoveToRecyclerBin(moveToRecyclerBin)

    var slideRandomPlay: Boolean
        get() = AppSharePreference.getInstance(context).getSlideRandomPlay(SlideRandomPlay.ENABLE)
        set(slideRandomPlay) = AppSharePreference.getInstance(context)
            .setSlideRandomPlay(slideRandomPlay)

    var shakeClose: Boolean
        get() = AppSharePreference.getInstance(context).getShakeClose(ShakeClose.ENABLE)
        set(shakeClose) = AppSharePreference.getInstance(context).setClose(shakeClose)

    var playVideoMode: Boolean
        get() = AppSharePreference.getInstance(context).getPlayVideoMode(PlayVideoMode.ENABLE)
        set(playVideoMode) = AppSharePreference.getInstance(context).setPlayVideoMode(playVideoMode)

    var encryptionMode: Int
        get() = AppSharePreference.getInstance(context).getEncryptionMode(EncryptionMode.ALWAYS_ASK)
        set(encryptionMode) = AppSharePreference.getInstance(context)
            .setEncryptionMode(encryptionMode)


    var externalStoragePath: String
        get() = AppSharePreference.getInstance(context)
            .getExternalStoragePath(getDefaultInternalPath())
        set(externalStoragePath) = AppSharePreference.getInstance(context)
            .setExternalStoragePath(externalStoragePath)

    var listItemVault: List<ListItem>?
        get() = AppSharePreference.getInstance(context)
            .getObjectFromSharePreference<List<ListItem>>(Constant.KEY_LIST_ITEM_VAULT)
        set(listItemVault) = AppSharePreference.getInstance(context)
            .saveObjectToSharePreference(Constant.KEY_LIST_ITEM_VAULT, listItemVault)

    var listPathRecyclerBin: List<ItemMoved>?
        get() = AppSharePreference.getInstance(context)
            .getObjectFromSharePreference<List<ItemMoved>>(Constant.KEY_LIST_RECYCLER_BIN)
        set(listPathRecyclerBin) = AppSharePreference.getInstance(context)
            .saveObjectToSharePreference(Constant.KEY_LIST_RECYCLER_BIN, listPathRecyclerBin)

    var hideAppIcon: Boolean
        get() = AppSharePreference.getInstance(context).getHideAppIcon(HideAppIcon.OFF)
        set(hideAppIcon) = AppSharePreference.getInstance(context).setHideAppIcon(hideAppIcon)

    var unlockAfterDialing: Boolean
        get() = AppSharePreference.getInstance(context)
            .getUnlockAfterDialing(UnlockAfterDialing.ENABLE)
        set(unlockAfterDialing) = AppSharePreference.getInstance(context)
            .setUnlockAfterDialing(unlockAfterDialing)

    var changeCalculatorIcon: Int
        get() = AppSharePreference.getInstance(context)
            .getChangeCalculatorIcon(ChangeCalculatorIcon.ICON_DEFAULT)
        set(changeCalculatorIcon) = AppSharePreference.getInstance(context)
            .setChangeCalculatorIcon(changeCalculatorIcon)

    var buttonToUnlock: Int
        get() = AppSharePreference.getInstance(context)
            .getButtonToUnlock(ButtonToUnlock.SHORT_PRESS)
        set(buttonToUnlock) = AppSharePreference.getInstance(context)
            .setButtonToUnlock(buttonToUnlock)

    var prohibitUnlockingByLongPressTitle: Boolean
        get() = AppSharePreference.getInstance(context)
            .getProhibitUnlockingByLongPressTitle(UnlockByLongPress.DISABLE)
        set(prohibitUnlockingByLongPressTitle) = AppSharePreference.getInstance(context)
            .setProhibitUnlockingByLongPressTitle(prohibitUnlockingByLongPressTitle)

    var unlockByFingerprint: Boolean
        get() = AppSharePreference.getInstance(context)
            .getUnlockByFingerprint(UnlockByFingerprint.DISABLE)
        set(unlockByFingerprint) = AppSharePreference.getInstance(context)
            .setUnlockByFingerprint(unlockByFingerprint)

    var fingerprintFailure: Boolean
        get() = AppSharePreference.getInstance(context)
            .getFingerprintFailure(FingerprintFailure.DISABLE)
        set(fingerprintFailure) = AppSharePreference.getInstance(context)
            .setFingerprintFailure(fingerprintFailure)

    var secretKey: String
        get() = AppSharePreference.getInstance(context).getSecretKey(Constant.SECRET_KEY)
        set(secretKey) = AppSharePreference.getInstance(context).setSecretKey(secretKey)

    var shakeGravity: Float
        get() = AppSharePreference.getInstance(context).getShakeGravity(2.0f)
        set(shakeGravity) = AppSharePreference.getInstance(context).setShakeGravity(shakeGravity)

    var repeat: Int
        get() = AppSharePreference.getInstance(context).getRepeatType(RepeatMode.REPEAT_ALL)
        set(repeat) = AppSharePreference.getInstance(context).setRepeatType(repeat)


    var prohibitScreenShot: Boolean
        get() = AppSharePreference.getInstance(context)
            .getProhibitScreenShot(ProhibitScreenShot.DISABLE)
        set(prohibitScreenShot) = AppSharePreference.getInstance(context)
            .setProhibitScreenShot(prohibitScreenShot)

    var photoIntruder: Boolean
        get() = AppSharePreference.getInstance(context).getPhotoIntruder(PhotoIntruder.DISABLE)
        set(photoIntruder) = AppSharePreference.getInstance(context).setPhotoIntruder(photoIntruder)

    var fakePassword: Boolean
        get() = AppSharePreference.getInstance(context).getFakePassword(FakePassword.DISABLE)
        set(fakePassword) = AppSharePreference.getInstance(context).setFakePassword(fakePassword)


    private fun getDefaultInternalPath(): String {
        val externalDir = context.getExternalFilesDir(null)

        return if (buildMinVersionQ()) externalDir?.parentFile?.parentFile?.parentFile?.parentFile?.path.toString()
        else Environment.getExternalStorageDirectory().path
    }


}