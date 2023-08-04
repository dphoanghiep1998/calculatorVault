package com.neko.hiepdph.calculatorvault

//import com.adjust.sdk.Adjust
//import com.adjust.sdk.AdjustConfig
//import com.adjust.sdk.LogLevel
//import com.applovin.sdk.AppLovinMediationProvider
//import com.applovin.sdk.AppLovinSdk
//import com.neko.hiepdph.calculatorvault.activities.MainActivity
//import com.neko.hiepdph.calculatorvault.common.utils.AudienceNetworkInitializeHelper
//import com.facebook.appevents.AppEventsLogger
//import com.gianghv.libads.AppOpenAdManager
//import com.gianghv.libads.InterstitialPreloadAdManager
//import com.gianghv.libads.InterstitialSingleReqAdManager
//import com.gianghv.libads.utils.Constants
//import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.RequestConfiguration
//import com.google.android.gms.ads.nativead.NativeAd
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.isScreenOn
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityCalculator
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityPatternLock
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityPinLock
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import kotlin.math.log

@HiltAndroidApp
class CustomApplication : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var currentActivity: Activity? = null
     var authority = false
     var isLockShowed = false
     var firstTimeOpen = true
     var changePassFail = false
//    private var appOpenAdsManager: AppOpenAdManager? = null
//    var shouldDestroyApp = false
//    var showAdsClickBottomNav = false
//    var nativeAD: NativeAd? = null
//    var interAdsIntro: InterstitialPreloadAdManager? = null
//    var settingLanguageLocale = ""


    override fun onCreate() {
        super.onCreate()
        AppSharePreference.getInstance(applicationContext)

//        settingLanguageLocale =
//            AppSharePreference.INSTANCE.getSavedLanguage(Locale.getDefault().language)
        registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//        MobileAds.initialize(this) { MobileAds.setAppMuted(true) }
//        val requestConfiguration =
//            RequestConfiguration.Builder().setTestDeviceIds(Constants.testDevices()).build()
//        MobileAds.setRequestConfiguration(requestConfiguration)
//        initAdjust()
//        initFBApp()
        initApplovinMediation()
        initOpenAds()
    }

    private fun initFBApp() {
//        AudienceNetworkInitializeHelper.initialize(applicationContext)
//        AppEventsLogger.activateApp(this)
    }

    private fun initApplovinMediation() {
//        AppLovinSdk.getInstance(this).mediationProvider = AppLovinMediationProvider.MAX
//        AppLovinSdk.getInstance(this).initializeSdk {}
    }

    private fun initAdjust() {
//        val config = AdjustConfig(
//            this, BuildConfig.adjust_token_key, AdjustConfig.ENVIRONMENT_PRODUCTION
//        )
//        config.setLogLevel(LogLevel.WARN)
//        Adjust.onCreate(config)
//        registerActivityLifecycleCallbacks(
//            this
//        )
    }

    private fun initOpenAds() {
//        appOpenAdsManager = AppOpenAdManager(
//            this, BuildConfig.open_app_id, BuildConfig.open_app_id, BuildConfig.open_app_id
//        )
//        appOpenAdsManager?.loadAd()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onMoveToForeground() {
        if(currentActivity !is ActivityPinLock && currentActivity !is ActivityPatternLock && currentActivity !is ActivityCalculator){
            if(authority && config.lockWhenLeavingApp && isLockShowed){
                val intent  = Intent(applicationContext,ActivityPinLock::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }


//        if (!InterstitialPreloadAdManager.isShowingAds && !InterstitialSingleReqAdManager.isShowingAds) {
//            currentActivity?.let {
//                if (currentActivity is MainActivity) {
//                    appOpenAdsManager?.showAdIfAvailable(it)
//                }
//            }
//        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onChangePause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onChangeStop() {
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
//        Adjust.onResume()

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        newConfig?.let {
            val uiMode = it.uiMode
            it.setTo(baseContext.resources.configuration)
            it.uiMode = uiMode
        }
    }

}