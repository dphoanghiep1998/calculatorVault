package com.neko.hiepdph.calculatorvault.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.neko.hiepdph.calculatorvault.CustomApplication
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.buildMinVersionS
import com.neko.hiepdph.calculatorvault.common.utils.openLink
import com.neko.hiepdph.calculatorvault.config.LockType
import com.neko.hiepdph.calculatorvault.config.LockWhenLeavingApp
import com.neko.hiepdph.calculatorvault.config.ScreenOffAction
import com.neko.hiepdph.calculatorvault.databinding.ActivityVaultBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemNavigationViewBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogRateUs
import com.neko.hiepdph.calculatorvault.dialog.DialogRequestPermission
import com.neko.hiepdph.calculatorvault.dialog.DialogShowIntruder
import com.neko.hiepdph.calculatorvault.dialog.RateCallBack
import com.neko.hiepdph.calculatorvault.shake.ShakeDetector
import com.neko.hiepdph.calculatorvault.viewmodel.VaultViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.system.exitProcess


@AndroidEntryPoint
class ActivityVault : AppCompatActivity() {
    private lateinit var binding: ActivityVaultBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private var screenOffBroadcastReceiver: BroadcastReceiver? = null
    private val viewModel by viewModels<VaultViewModel>()
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: ShakeDetector? = null
    private var dialogRequestPermission: DialogRequestPermission? = null
    private fun setThemeMode() {
        if (config.darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(
            mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI
        )
        if (!config.isShowLock) {
            when (config.lockType) {
                LockType.PATTERN -> {
                    startActivity(Intent(this@ActivityVault, ActivityPatternLock::class.java))
                    finish()
                }

                LockType.PIN -> {
                    startActivity(Intent(this@ActivityVault, ActivityPinLock::class.java))
                    finish()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigationView()
        initScreenOffAction()
        setupNavigationDrawer()
        setSupportActionBar(binding.toolbar)
        setupActionBar()
        checkPermissionAllFileManage()
        checkIntruderByPass()
        initShakeDetector()
        setThemeMode()
        registerBroadcastHideApp()
    }

    private fun checkIntruderByPass() {
        if ((application as CustomApplication).authority && config.caughtIntruder) {
            val dialogShowIntruder = DialogShowIntruder()
            dialogShowIntruder.show(supportFragmentManager, dialogShowIntruder.tag)
            config.caughtIntruder = false
        }
    }

    private fun checkPermissionAllFileManage() {
        if (buildMinVersionS()) {
            val hasManageExternalStoragePermission = Environment.isExternalStorageManager()
            if (hasManageExternalStoragePermission) {
                createSecretFolderFirstTime()
            } else {
                dialogRequestPermission = DialogRequestPermission(onClickPositive = {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse("package:$packageName")
                    launcher.launch(intent)
                })
                dialogRequestPermission?.show(supportFragmentManager, dialogRequestPermission?.tag)

            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                createSecretFolderFirstTime()
            } else {
                dialogRequestPermission = DialogRequestPermission(onClickPositive = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                })
                dialogRequestPermission?.show(supportFragmentManager, dialogRequestPermission?.tag)

            }
        }
    }

    private fun initNavigationView() {
        if (config.fakePassword) {
            if (!(application as CustomApplication).authority) {
                binding.itemVault.root.hide()
                binding.itemRecyclerBin.root.hide()
                binding.itemSetting.root.hide()
                binding.itemPrivacy.root.hide()
            } else {
                binding.itemVault.root.show()
                binding.itemRecyclerBin.root.show()
                binding.itemSetting.root.show()
                binding.itemPrivacy.root.show()
            }
        } else {
            binding.itemVault.root.show()
            binding.itemRecyclerBin.root.show()
            binding.itemSetting.root.show()
            binding.itemPrivacy.root.show()
        }
        binding.itemVault.apply {
            imvIcon.setImageResource(R.drawable.ic_vault)
            tvContent.text = getString(R.string.vault)
            root.clickWithDebounce {
                findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentVault)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.itemBrowser.apply {
            imvIcon.setImageResource(R.drawable.ic_browser)
            tvContent.text = getString(R.string.browser)
            root.clickWithDebounce {
                findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentBrowser)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.itemNote.apply {
            imvIcon.setImageResource(R.drawable.ic_note)
            tvContent.text = getString(R.string.note)
            root.clickWithDebounce {
                findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentNote)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.itemRecyclerBin.apply {
            imvIcon.setImageResource(R.drawable.ic_delete)
            tvContent.text = getString(R.string.recycle_bin)
            root.clickWithDebounce {
                findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentRecycleBin)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.itemSetting.apply {
            imvIcon.setImageResource(R.drawable.ic_setting)
            tvContent.text = getString(R.string.setting)
            root.clickWithDebounce {
                findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentSetting)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.itemLanguage.apply {
            imvIcon.setImageResource(R.drawable.ic_language)
            tvContent.text = getString(R.string.language)
            root.clickWithDebounce {
                findNavController(R.id.nav_host_fragment).navigate(R.id.fragmentLanguage)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.itemTheme.apply {
            binding.iconTheme.setImageResource(R.drawable.ic_dark_theme)
            binding.contentTheme.text = getString(R.string.dark_theme)
            binding.switchMenu.isChecked = config.darkMode
            clickWithDebounce {
                binding.switchMenu.isChecked = !binding.switchMenu.isChecked
            }
        }
        binding.switchMenu.setOnClickListener {
            changeThemeMode(binding.switchMenu.isChecked)
        }
        binding.itemRateApp.apply {
            imvIcon.setImageResource(R.drawable.ic_rate)
            tvContent.text = getString(R.string.rate_app)
            root.clickWithDebounce {
                rateApp()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.itemShareApp.apply {
            imvIcon.setImageResource(R.drawable.ic_share)
            tvContent.text = getString(R.string.share_app)
            root.clickWithDebounce {
                shareApp()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
//        binding.itemAboutUs.apply {
//            imvIcon.setImageResource(R.drawable.ic_info)
//            tvContent.text = getString(R.string.about_us)
//            root.clickWithDebounce {
//                binding.drawerLayout.closeDrawer(GravityCompat.START)
//            }
//        }
        binding.itemPrivacy.apply {
            imvIcon.setImageResource(R.drawable.ic_security_private)
            tvContent.text = getString(R.string.privacy_policy)
            root.clickWithDebounce {
                openLink(Constant.URL_PRIVACY)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.fragmentVault -> {
                    resetBackground(binding.itemVault)
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                R.id.fragmentBrowser -> {
                    resetBackground(binding.itemBrowser)
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                R.id.fragmentNote -> {
                    resetBackground(binding.itemNote)
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                R.id.fragmentRecycleBin -> {
                    resetBackground(binding.itemRecyclerBin)
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                R.id.fragmentSetting -> {
                    resetBackground(binding.itemSetting)
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                R.id.fragmentLanguage -> {
                    resetBackground(binding.itemLanguage)
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                else -> {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
        }

    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT, Constant.URL_APP
            )
            startActivity(Intent.createChooser(shareIntent, "Choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun rateApp() {
        val dialogRate = DialogRateUs(callBack = object : RateCallBack {
            override fun onNegativePressed() {
            }

            override fun onPositivePressed(star: Int) {
                AppSharePreference.INSTANCE.saveUserRate(star)
                if (star >= 4) {
                    openLink(Constant.URL_APP)
                }
                showSnackBar(getString(R.string.rate_success), SnackBarType.SUCCESS)
            }

        })
        dialogRate.show(supportFragmentManager, dialogRate.tag)
    }

    private fun resetBackground(item: LayoutItemNavigationViewBinding) {
        val listItem = mutableListOf(
            binding.itemVault,
            binding.itemBrowser,
            binding.itemNote,
            binding.itemRecyclerBin,
            binding.itemSetting,
            binding.itemLanguage
        )
        item.root.background =
            ContextCompat.getDrawable(this, R.drawable.bg_navigation_item_seleted)
        item.root.backgroundTintList = AppCompatResources.getColorStateList(this, R.color.secondary)
        item.imvIcon.imageTintList = AppCompatResources.getColorStateList(this, R.color.primary)
        item.tvContent.setTextColor(getColor(R.color.primary))
        listItem.filter { it != item }.forEach { it1 ->
            it1.root.background = null
            it1.root.backgroundTintList =
                AppCompatResources.getColorStateList(this, R.color.neutral_06)
            it1.imvIcon.imageTintList =
                AppCompatResources.getColorStateList(this, R.color.neutral_06)
            it1.tvContent.setTextColor(getColor(R.color.neutral_06))
        }
    }

    private fun changeThemeMode(darkMode: Boolean) {
        config.darkMode = darkMode
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun createSecretFolderFirstTime() {
        if (!AppSharePreference.INSTANCE.getInitDone(false)) {
            viewModel.createFolder(filesDir, Constant.PICTURE_FOLDER_NAME)
            viewModel.createFolder(filesDir, Constant.VIDEOS_FOLDER_NAME)
            viewModel.createFolder(filesDir, Constant.AUDIOS_FOLDER_NAME)
            viewModel.createFolder(filesDir, Constant.FILES_FOLDER_NAME)
            viewModel.createFolder(File(config.externalStoragePath), Constant.DECRYPT_FOLDER_NAME)
            viewModel.createFolder(filesDir, Constant.INTRUDER_FOLDER_NAME)
            viewModel.createFolder(filesDir, Constant.RECYCLER_BIN_FOLDER_NAME)

            AppSharePreference.INSTANCE.saveInitFirstDone(true)
            viewModel.getListFolderInVault(this, filesDir)
        }
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (buildMinVersionS()) {
                val hasManageExternalStoragePermission = Environment.isExternalStorageManager()

                if (hasManageExternalStoragePermission) {
                    createSecretFolderFirstTime()
                }
            }

        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            dialogRequestPermission?.dismiss()
            createSecretFolderFirstTime()
        }

    private fun initShakeDetector() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector(this)
        mShakeDetector?.setOnShakeListener(object : ShakeDetector.OnShakeListener {
            override fun onShake(count: Int) {
                finishAffinity()
                exitProcess(-1)
            }

        })

    }


    fun setupActionBar() {
        val navController: NavController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.fragmentVault,
            R.id.fragmentBrowser,
            R.id.fragmentNote,
            R.id.fragmentRecycleBin,
            R.id.fragmentSetting,
            R.id.fragmentLanguage,
        ).setOpenableLayout(binding.drawerLayout).build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
    }

    private fun initScreenOffAction() {
        screenOffBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1?.action == Intent.ACTION_SCREEN_ON) {
                    when (config.screenOffAction) {
                        ScreenOffAction.GOTOHOMESCREEN -> {
                            val intent = Intent(this@ActivityVault, ActivityCalculator::class.java)
                            startActivity(intent)
                            finish()
                        }

                        ScreenOffAction.LOCKAGAIN -> {
                            when (config.lockType) {
                                LockType.PIN -> {
                                    val intent =
                                        Intent(this@ActivityVault, ActivityPinLock::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                                LockType.PATTERN -> {
                                    val intent =
                                        Intent(this@ActivityVault, ActivityPatternLock::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                                else -> {
                                    //do nothing
                                }
                            }
                        }

                        ScreenOffAction.NOACTION -> {
                            //do nothing
                        }
                    }
                }
            }

        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenOffBroadcastReceiver, intentFilter)
    }

    private fun removeScreenOffAction() {
        unregisterReceiver(screenOffBroadcastReceiver)
    }

    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout.apply {
            setStatusBarBackground(R.color.purple_200)
        }

    }

    @SuppressLint("InlinedApi")
    private fun registerBroadcastHideApp() {
        val hideAppReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("TAG", "onReceive: ")
                if (intent!!.action.equals(TelephonyManager.ACTION_SECRET_CODE)) {
                    val code = intent.data?.host
                    if (code == "0101") {
                        if (config.unlockAfterDialing) {
                            if (config.lockType == LockType.PIN) {
                                val launchIntent = Intent(context, ActivityPinLock::class.java)
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context!!.startActivity(launchIntent)
                            } else {
                                val launchIntent = Intent(context, ActivityPatternLock::class.java)
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context!!.startActivity(launchIntent)
                            }
                        } else {
                            val launchIntent = Intent(context, ActivityVault::class.java)
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context!!.startActivity(launchIntent)
                        }
                    }
                }
            }

        }
        val intentFilter = IntentFilter().apply {
            addDataScheme("android_secret_code")
            addAction(TelephonyManager.ACTION_SECRET_CODE)
        }
        registerReceiver(hideAppReceiver, intentFilter)
    }


    fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun onSupportNavigateUp(): Boolean {
        return (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController.navigateUp(
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        removeScreenOffAction()
        mSensorManager?.unregisterListener(mShakeDetector);
        super.onDestroy()
    }


}