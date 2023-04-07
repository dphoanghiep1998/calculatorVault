package com.neko.hiepdph.calculatorvault.common.extensions

//import com.gianghv.libads.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.neko.hiepdph.calculatorvault.R
import kotlinx.coroutines.launch

enum class NativeType {
    HISTORY, INFORMATION, SETTINGS, LANGUAGE, RECORD, DETAIL_INFORMATION, UNIT, TARGET
}

enum class InterAds {
    SPLASH, INTRO, NEW_RECORD, SWITCH_TAB, DETAIL_INFO
}

enum class SnackBarType {
    SUCCESS, FAILED
}


fun Fragment.navigateToPage(id: Int, actionId: Int, bundle: Bundle? = null) {
    viewLifecycleOwner.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            Log.d("TAG", "navigateToPage: " + id)
            if (findNavController().currentDestination?.id == id && isAdded) {
                findNavController().navigate(
                    actionId, bundle
                )
            }
        }

    }
}

fun Fragment.navigateToPage(id: Int, navDirections: NavDirections) {
    viewLifecycleOwner.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (findNavController().currentDestination?.id == id && isAdded) {
                findNavController().navigate(navDirections)
            }
        }
    }

}

fun Fragment.popBackStack() {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            findNavController().popBackStack()
        }
    }


}

fun Fragment.getColor(res: Int): Int {
    return ContextCompat.getColor(requireContext(), res)
}


@SuppressLint("ResourceAsColor")
fun Fragment.showSnackBar(text: String, type: SnackBarType) {
    val snackBar: Snackbar =
        Snackbar.make(requireActivity().window.decorView, text, Snackbar.LENGTH_SHORT)
    snackBar.setAction("Action", null)
    when (type) {
        SnackBarType.FAILED -> snackBar.setBackgroundTint(requireContext().getColor(R.color.theme_01))
        SnackBarType.SUCCESS -> snackBar.setBackgroundTint(requireContext().getColor(R.color.theme_08))

    }
    val sbView = snackBar.view
    val textView =
        sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
    drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.white))
    textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    textView.compoundDrawablePadding =
        requireContext().resources.getDimensionPixelOffset(R.dimen.snackbar_icon_padding)
    snackBar.show()
}

fun Fragment.openLink(strUri: String?) {
    try {
        val uri = Uri.parse(strUri)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Fragment.changeBackPressCallBack(action: () -> Unit) {
    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action.invoke()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}

fun Fragment.setStatusColor(color: Int) {

    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), color)

}

fun hideSoftKeyboard(activity: Activity, view: View) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    if (inputMethodManager.isAcceptingText) {
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken, 0
        )
    }
    view.clearFocus()
}


fun showSoftKeyboard(activity: Activity, view: View) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager

    inputMethodManager.showSoftInput(
        view, 0
    )
}

@SuppressLint("ClickableViewAccessibility")
fun Fragment.setupUI(view: View) {
    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { _, _ ->
            hideSoftKeyboard(requireActivity(), view)
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            setupUI(innerView)
        }
    }
}


//fun isInternetAvailable(context: Context): Boolean {
//    if (buildMinVersionM()) {
//        var result = false
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//        cm?.run {
//            this.getNetworkCapabilities(this.activeNetwork)?.run {
//                result = when {
//                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//                    else -> false
//                }
//            }
//        }
//        return result
//    } else {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val netInfo = cm.activeNetworkInfo
//        if (netInfo != null) {
//            val networkInfo = cm.activeNetworkInfo
//            return networkInfo != null && networkInfo.isConnected
//        }
//        return false
//    }
//}
//
//fun Fragment.showNativeAds(
//    view: NativeAdMediumView?,
//    view_small: NativeAdSmallView?,
//    action: (() -> Unit)? = null,
//    action_fail: (() -> Unit)? = null,
//    type: NativeType
//) {
//    val mNativeAdManager: NativeAdsManager?
//    when (type) {
//        NativeType.HISTORY -> {
//            mNativeAdManager = NativeAdsManager(
//                requireActivity(),
//                BuildConfig.native_history_id1,
//                BuildConfig.native_history_id2,
//                BuildConfig.native_history_id3,
//            )
//        }
//        NativeType.INFORMATION -> {
//            mNativeAdManager = NativeAdsManager(
//                requireActivity(),
//                BuildConfig.native_info_id1,
//                BuildConfig.native_info_id2,
//                BuildConfig.native_info_id3,
//            )
//        }
//        NativeType.SETTINGS -> {
//            mNativeAdManager = NativeAdsManager(
//                requireActivity(),
//                BuildConfig.native_settings_id1,
//                BuildConfig.native_settings_id2,
//                BuildConfig.native_settings_id3,
//            )
//        }
//        NativeType.LANGUAGE -> {
//            mNativeAdManager = NativeAdsManager(
//                requireActivity(),
//                BuildConfig.native_languages_id1,
//                BuildConfig.native_languages_id2,
//                BuildConfig.native_languages_id3,
//            )
//        }
//
//        NativeType.RECORD -> {
//            mNativeAdManager = NativeAdsManager(
//                requireActivity(),
//                BuildConfig.native_record_id1,
//                BuildConfig.native_record_id2,
//                BuildConfig.native_record_id3,
//            )
//        }
//        NativeType.DETAIL_INFORMATION -> {
//            mNativeAdManager = NativeAdsManager(
//                requireActivity(),
//                BuildConfig.native_info_detail_id1,
//                BuildConfig.native_info_detail_id2,
//                BuildConfig.native_info_detail_id3,
//            )
//        }
//        NativeType.TARGET -> {
//            mNativeAdManager = NativeAdsManager(
//                requireActivity(),
//                BuildConfig.native_target_id1,
//                BuildConfig.native_target_id2,
//                BuildConfig.native_target_id3,
//            )
//        }
//
//        NativeType.UNIT -> {
//            mNativeAdManager = NativeAdsManager(
//                requireActivity(),
//                BuildConfig.native_unit_id1,
//                BuildConfig.native_unit_id2,
//                BuildConfig.native_unit_id3,
//            )
//        }
//
//    }
//    view?.let {
//        it.showShimmer(true)
//        mNativeAdManager.loadAds(onLoadSuccess = { nativeAd ->
//            it.visibility = View.VISIBLE
//            action?.invoke()
//            it.showShimmer(false)
//            it.setNativeAd(nativeAd)
//            it.isVisible = true
//        }, onLoadFail = { _ ->
//            action_fail?.invoke()
//            it.errorShimmer()
//            it.visibility = View.GONE
//        })
//    }
//
//    view_small?.let {
//        it.showShimmer(true)
//        mNativeAdManager.loadAds(onLoadSuccess = { nativeAd ->
//            it.visibility = View.VISIBLE
//            action?.invoke()
//            it.showShimmer(false)
//            it.setNativeAd(nativeAd)
//            it.isVisible = true
//        }, onLoadFail = { _ ->
//            it.errorShimmer()
//            it.visibility = View.GONE
//        })
//    }
//
//}
//
//fun Fragment.showBannerAds(view: ViewGroup, action: (() -> Unit)? = null) {
//    val adaptiveBannerManager = AdaptiveBannerManager(
//        requireActivity(),
//        BuildConfig.banner_home_id1,
//        BuildConfig.banner_home_id2,
//        BuildConfig.banner_home_id3,
//    )
//    adaptiveBannerManager.loadBanner(view,
//        onAdLoadFail = { action?.invoke() },
//        onAdLoader = { action?.invoke() })
//}
//
//fun Fragment.showInterAds(
//    action: () -> Unit, type: InterAds
//) {
//    if (!isAdded) {
//        action.invoke()
//        return
//    }
//    if (!isInternetAvailable(requireContext())) {
//        action.invoke()
//        return
//    }
//    val interstitialSingleReqAdManager: InterstitialSingleReqAdManager
//    when (type) {
//        InterAds.SPLASH -> {
//            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
//                requireActivity(),
//                BuildConfig.inter_splash_id1,
//                BuildConfig.inter_splash_id2,
//                BuildConfig.inter_splash_id3,
//            )
//        }
//        InterAds.SWITCH_TAB -> {
//            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
//                requireActivity(),
//                BuildConfig.inter_switch_tab_id1,
//                BuildConfig.inter_switch_tab_id2,
//                BuildConfig.inter_switch_tab_id3,
//            )
//        }
//        InterAds.INTRO -> {
//            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
//                requireActivity(),
//                BuildConfig.inter_intro_id1,
//                BuildConfig.inter_intro_id2,
//                BuildConfig.inter_intro_id3,
//            )
//        }
//
//        InterAds.DETAIL_INFO -> {
//            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
//                requireActivity(),
//                BuildConfig.inter_info_back_id1,
//                BuildConfig.inter_info_back_id2,
//                BuildConfig.inter_info_back_id3,
//            )
//        }
//        InterAds.NEW_RECORD -> {
//            interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
//                requireActivity(),
//                BuildConfig.inter_new_record_id1,
//                BuildConfig.inter_new_record_id2,
//                BuildConfig.inter_new_record_id3,
//            )
//        }
//
//    }
//    val dialogLoadingInterAds: DialogLoadingInterAds? = DialogLoadingInterAds(requireContext())
//    dialogLoadingInterAds?.showDialog()
//    InterstitialSingleReqAdManager.isShowingAds = true
//
//    interstitialSingleReqAdManager.showAds(requireActivity(), onLoadAdSuccess = {
//        dialogLoadingInterAds?.hideDialog()
//    }, onAdClose = {
//        InterstitialSingleReqAdManager.isShowingAds = false
//        action()
//        dialogLoadingInterAds?.hideDialog()
//    }, onAdLoadFail = {
//        InterstitialSingleReqAdManager.isShowingAds = false
//        action()
//        dialogLoadingInterAds?.hideDialog()
//    })
//
//}
