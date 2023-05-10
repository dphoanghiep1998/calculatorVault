package com.neko.hiepdph.calculatorvault.common.share_preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neko.hiepdph.calculatorvault.common.Constant
import java.lang.reflect.Type


inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor: SharedPreferences.Editor = edit()
    editor.func()
    editor.apply()
}


class AppSharePreference(private val context: Context?) {
    companion object {
        lateinit var INSTANCE: AppSharePreference

        @JvmStatic
        fun getInstance(context: Context?): AppSharePreference {
            if (!Companion::INSTANCE.isInitialized) {
                INSTANCE = AppSharePreference(context)
            }
            return INSTANCE
        }

    }


    inline fun <reified T> saveObjectToSharePreference(key: String, data: T) {
        val gson = Gson()
        val json = gson.toJson(data)
        sharedPreferences().edit().putString(key, json).apply()
    }

    inline fun <reified T> getObjectFromSharePreference(key: String): T? {
        val serializedObject: String? = sharedPreferences().getString(key, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<T?>() {}.type
            return gson.fromJson(serializedObject, type)
        }
        return null
    }

    fun setThemeColor(values: Int) {
        saveInt(Constant.KEY_THEME_COLOR, values)
    }

    fun getThemeColor(defaultValues: Int): Int {
        return getInt(Constant.KEY_THEME_COLOR, defaultValues)
    }

    fun setSecurityQuestion(values: String) {
        saveString(Constant.KEY_SECURITY_QUESTION, values)
    }

    fun getSecurityQuestion(defaultValues: String): String {
        return getString(Constant.KEY_SECURITY_QUESTION, defaultValues)
    }

    fun setSecurityAnswer(values: String) {
        saveString(Constant.KEY_SECURITY_ANSWER, values)
    }

    fun getSecurityAnswer(defaultValues: String): String {
        return getString(Constant.KEY_SECURITY_ANSWER, defaultValues)
    }

    fun setFaqClicked(values: Boolean) {
        saveBoolean(Constant.KEY_FAQ_CLICKED, values)
    }

    fun getFaqClicked(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_FAQ_CLICKED, defaultValues)
    }

    fun saveIsSetLangFirst(values: Boolean) {
        saveBoolean(Constant.KEY_SET_LANG, values)
    }

    fun getSetLangFirst(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_SET_LANG, defaultValues)
    }


    fun saveUserRate(values: Int) {
        saveInt(Constant.KEY_USER_RATE, values)
    }

    fun getUserRate(defaultValues: Int): Int {
        return getInt(Constant.KEY_USER_RATE, defaultValues)
    }

    fun saveNumNotify(values: Int) {
        saveInt(Constant.KEY_NUM_NOTI, values)
    }

    fun getNumNotify(defaultValues: Int): Int {
        return getInt(Constant.KEY_NUM_NOTI, defaultValues)
    }

    fun saveInitFirstDone(values: Boolean) {
        saveBoolean(Constant.KEY_FIRST_INIT, values)
    }

    fun getIsPassedSplashScreen(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_PASS_SPLASH, defaultValues)
    }

    fun saveIsPassedSplashScreen(values: Boolean) {
        saveBoolean(Constant.KEY_PASS_SPLASH, values)
    }

    fun saveFirstDataInject(values: Boolean) {
        saveBoolean(Constant.KEY_FIRST_DATA_INJECT, values)
    }

    fun getFirstDataInject(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_FIRST_DATA_INJECT, defaultValues)
    }

    fun getInitDone(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_FIRST_INIT, defaultValues)
    }

    fun saveTimeToShowRate(values: Long) {
        saveLong(Constant.KEY_TIME_RATE, values)
    }

    fun getTimeToShowRate(defaultValues: Long): Long {
        return getLong(Constant.KEY_TIME_RATE, defaultValues)
    }


    fun saveLanguage(values: String) {
        saveString(Constant.KEY_LANGUAGE, values)
    }

    fun getSavedLanguage(defaultValues: String): String {
        return getString(Constant.KEY_LANGUAGE, defaultValues)
    }


    fun saveListNote(values: List<String>) {
        saveStringList(Constant.KEY_NOTE_LIST, values)
    }

    fun getListNote(defaultValues: ArrayList<String>): ArrayList<String> {
        return getStringList(Constant.KEY_NOTE_LIST, defaultValues)
    }

    fun saveListIndexNotification(values: List<String>) {
        saveStringList(Constant.KEY_INDEX_NOTIFY, values)
    }

    fun getListIndexNotification(defaultValues: ArrayList<String>): ArrayList<String> {
        return getStringList(Constant.KEY_INDEX_NOTIFY, defaultValues)
    }

    private fun saveLong(key: String, values: Long) = sharedPreferences().edit {
        putLong(key, values)
    }

    private fun getLong(key: String, defaultValues: Long): Long {
        return try {
            sharedPreferences().getLong(key, defaultValues)
        } catch (e: Exception) {
            sharedPreferences().edit { putLong(key, defaultValues) }
            defaultValues
        }
    }

    fun getFingerPrintLockDisplay(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_FINGER_PRINT_DISPLAY, defaultValues)
    }

    fun saveFingerPrintLockDisplay(values: Boolean) {
        saveBoolean(Constant.KEY_FINGER_PRINT_DISPLAY, values)
    }

    fun getIsShouldShowHidden(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_IS_SHOULD_SHOW_HIDDEN, defaultValues)
    }

    fun setShouldShowHidden(values: Boolean) {
        saveBoolean(Constant.KEY_IS_SHOULD_SHOW_HIDDEN, values)
    }

    fun getLockType(defaultValues: Int): Int {
        return getInt(Constant.KEY_LOCK_TYPE, defaultValues)
    }

    fun saveLockType(values: Int) {
        saveInt(Constant.KEY_LOCK_TYPE, values)
    }

    fun getLockPressType(defaultValues: Int): Int {
        return getInt(Constant.KEY_LOCK_PRESS_TYPE, defaultValues)
    }

    fun saveLockPressType(values: Int) {
        saveInt(Constant.KEY_LOCK_PRESS_TYPE, values)
    }

    fun getFingerPrintUnlock(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_FINGER_PRINT_UNLOCK, defaultValues)
    }

    fun saveFingerPrintUnlock(values: Boolean) {
        saveBoolean(Constant.KEY_FINGER_PRINT_UNLOCK, values)
    }

    fun getLockWhenLeavingApp(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_LOCK_WHEN_LEAVING_APP, defaultValues)
    }

    fun saveLockWhenLeavingApp(values: Boolean) {
        saveBoolean(Constant.KEY_LOCK_WHEN_LEAVING_APP, values)
    }


    private fun saveInt(key: String, values: Int) = sharedPreferences().edit {
        putInt(key, values)
    }

    private fun getInt(key: String, defaultValues: Int): Int {
        return try {
            sharedPreferences().getInt(key, defaultValues)
        } catch (e: Exception) {
            sharedPreferences().edit { putInt(key, defaultValues) }
            defaultValues
        }
    }

    fun saveString(key: String, values: String): Unit =
        sharedPreferences().edit { putString(key, values) }

    fun getString(key: String, defaultValues: String): String {
        return try {
            sharedPreferences().getString(key, defaultValues)!!
        } catch (e: Exception) {
            sharedPreferences().edit { putString(key, defaultValues) }
            defaultValues
        }
    }

    private fun saveStringList(key: String, values: List<String>): Unit {
        val gson = Gson()
        sharedPreferences().edit { putString(key, gson.toJson(values)) }
    }

    private fun getStringList(key: String, defaultValues: ArrayList<String>): ArrayList<String> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return try {
            val returnString = sharedPreferences().getString(key, gson.toJson(defaultValues))
            gson.fromJson(returnString, type)
        } catch (e: Exception) {
            sharedPreferences().edit { putString(key, gson.toJson(defaultValues)) }
            defaultValues
        }
    }

    private fun saveBoolean(key: String, values: Boolean) {
        sharedPreferences().edit { putBoolean(key, values) }
    }

    private fun getBoolean(key: String, defaultValues: Boolean): Boolean {
        return try {
            sharedPreferences().getBoolean(key, defaultValues)
        } catch (e: Exception) {
            sharedPreferences().edit { putBoolean(key, defaultValues) }
            defaultValues
        }
    }

    private fun saveStringSet(key: String, values: HashSet<String>) {
        sharedPreferences().edit { putStringSet(key, values) }
    }

    private fun getStringSet(key: String, defaultValues: HashSet<String>): HashSet<String> {
        return try {
            sharedPreferences().getStringSet(key, defaultValues)!! as HashSet
        } catch (e: Exception) {
            sharedPreferences().edit { putStringSet(key, defaultValues) }
            defaultValues
        }
    }

    fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener): Unit =
        sharedPreferences().registerOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener
        )

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences().unregisterOnSharedPreferenceChangeListener(listener)
    }


    private fun defaultSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun sharedPreferences(): SharedPreferences = defaultSharedPreferences(context!!)
    fun getScreenOffAction(defaultValues: Int): Int {
        return getInt(Constant.KEY_SCREEN_OFF_ACTION, defaultValues)
    }

    fun saveScreenOffAction(values: Int) {
        saveInt(Constant.KEY_SCREEN_OFF_ACTION, values)
    }

    fun getVisiblePattern(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_VISIBLE_PATTERN, defaultValues)
    }

    fun saveVisiblePattern(values: Boolean) {
        saveBoolean(Constant.KEY_VISIBLE_PATTERN, values)
    }

    fun getTactileFeedback(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_TACTILE_FEEDBACK, defaultValues)
    }

    fun saveTactileFeedback(values: Boolean) {
        saveBoolean(Constant.KEY_TACTILE_FEEDBACK, values)
    }

    fun getSecretPin(defaultValues: String): String {
        return getString(Constant.KEY_SECRET_PIN, defaultValues)
    }


    fun saveSecretPin(values: String) {
        saveString(Constant.KEY_SECRET_PIN, values)
    }

    fun getPatternLock(defaultValues: ArrayList<String>): List<Int> {
        val stringList = getStringList(Constant.KEY_PATTERN_LOCK, defaultValues)
        return stringList.map {
            it.toInt()
        }
    }

    fun setPatternLock(patternLock: List<Int>) {
        saveStringList(Constant.KEY_PATTERN_LOCK, patternLock.map { it.toString() })
    }

    fun getShowLock(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_SHOW_LOCK, defaultValues)
    }

    fun setShowLock(values: Boolean) {
        saveBoolean(Constant.KEY_SHOW_LOCK, values)
    }

    fun getSlideShowInterval(defaultValues: Int): Int {
        return getInt(Constant.KEY_SLIDE_SHOW_INTERVAL, defaultValues)
    }

    fun setSlideShowInterval(value: Int) {
        saveInt(Constant.KEY_SLIDE_SHOW_INTERVAL, value)
    }

    fun getSlideShowTransition(defaultValues: Int): Int {
        return getInt(Constant.KEY_SLIDE_SHOW_TRANSITION, defaultValues)
    }

    fun setSlideShowTransition(values: Int) {
        saveInt(Constant.KEY_SLIDE_SHOW_TRANSITION, values)
    }

    fun getMoveToRecyclerBin(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_MOVE_TO_RECYCLER_BIN, defaultValues)
    }

    fun setMoveToRecyclerBin(values: Boolean) {
        saveBoolean(Constant.KEY_MOVE_TO_RECYCLER_BIN, values)
    }

    fun getSlideRandomPlay(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_SLIDE_RANDOM_PLAY, defaultValues)
    }

    fun setSlideRandomPlay(values: Boolean) {
        saveBoolean(Constant.KEY_SLIDE_RANDOM_PLAY, values)
    }

    fun getShakeClose(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_SHAKE_CLOSE, defaultValues)
    }

    fun setClose(values: Boolean) {
        saveBoolean(Constant.KEY_SHAKE_CLOSE, values)
    }

    fun getPlayVideoMode(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_PLAY_VIDEO_MODE, defaultValues)
    }

    fun setPlayVideoMode(values: Boolean) {
        saveBoolean(Constant.KEY_PLAY_VIDEO_MODE, values)
    }

    fun getEncryptionMode(defaultValues: Int): Int {
        return getInt(Constant.KEY_ENCRYPTION_MODE, defaultValues)
    }

    fun setEncryptionMode(values: Int) {
        saveInt(Constant.KEY_ENCRYPTION_MODE, values)
    }

    fun getExternalStoragePath(defaultExternalPath: String): String {
        return getString(Constant.EXTERNAL_STORAGE_PATH, defaultExternalPath)
    }

    fun setExternalStoragePath(values: String) {
        saveString(Constant.EXTERNAL_STORAGE_PATH, values)
    }

    fun getHideAppIcon(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_HIDE_APP_ICON, defaultValues)
    }

    fun setHideAppIcon(values: Boolean) {
        saveBoolean(Constant.KEY_HIDE_APP_ICON, values)
    }

    fun getUnlockAfterDialing(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_UNLOCK_AFTER_DIALING, defaultValues)
    }

    fun setUnlockAfterDialing(values: Boolean) {
        saveBoolean(Constant.KEY_UNLOCK_AFTER_DIALING, values)
    }

    fun getChangeCalculatorIcon(defaultValues: Int): Int {
        return getInt(Constant.KEY_CHANGE_ICON, defaultValues)
    }

    fun setChangeCalculatorIcon(values: Int) {
        saveInt(Constant.KEY_CHANGE_ICON, values)

    }

    fun getButtonToUnlock(defaultValues: Int): Int {
        return getInt(Constant.KEY_BUTTON_UNLOCK, defaultValues)
    }

    fun setButtonToUnlock(values: Int) {
        saveInt(Constant.KEY_BUTTON_UNLOCK, values)
    }

    fun getProhibitUnlockingByLongPressTitle(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_UNLOCK_BY_PRESS_TITLE, defaultValues)
    }

    fun setProhibitUnlockingByLongPressTitle(values: Boolean) {
        saveBoolean(Constant.KEY_UNLOCK_BY_PRESS_TITLE, values)
    }

    fun getUnlockByFingerprint(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_UNLOCK_BY_FINGERPRINT, defaultValues)
    }

    fun setUnlockByFingerprint(values: Boolean) {
        saveBoolean(Constant.KEY_UNLOCK_BY_FINGERPRINT, values)
    }

    fun getFingerprintFailure(defaultValues: Boolean): Boolean {
        return getBoolean(Constant.KEY_FINGERPRINT_FAILURE, defaultValues)
    }

    fun setFingerprintFailure(values: Boolean) {
        saveBoolean(Constant.KEY_FINGERPRINT_FAILURE, values)
    }

    fun getSecretKey(defaultValues: String): String {
        return getString(Constant.KEY_CIPHER_CODE, defaultValues)
    }

    fun setSecretKey(values: String) {
        saveString(Constant.KEY_CIPHER_CODE, values)

    }

    fun getShakeGravity(defaultValues: Float): Float {
        return getString(Constant.KEY_SHAKE_GRAVITY, defaultValues.toString()).toFloat()
    }

    fun setShakeGravity(values: Float) {
        saveString(Constant.KEY_SHAKE_GRAVITY, values.toString())
    }

    fun getRepeatType(defaultValues: Int): Int {
        return getInt(Constant.KEY_REPEAT, defaultValues)
    }

    fun setRepeatType(values: Int) {
        saveInt(Constant.KEY_REPEAT, values)
    }


}

