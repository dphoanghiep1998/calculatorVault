package com.neko.hiepdph.calculatorvault.ui.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.ICreateFile
import com.neko.hiepdph.calculatorvault.common.utils.buildMinVersionS
import com.neko.hiepdph.calculatorvault.config.LockType
import com.neko.hiepdph.calculatorvault.config.LockWhenLeavingApp
import com.neko.hiepdph.calculatorvault.config.ScreenOffAction
import com.neko.hiepdph.calculatorvault.databinding.ActivityVaultBinding
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel
import com.neko.hiepdph.calculatorvault.viewmodel.VaultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityVault : AppCompatActivity() {
    private lateinit var binding: ActivityVaultBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private var screenOffBroadcastReceiver: BroadcastReceiver? = null
    private val viewModel by viewModels<VaultViewModel>()

    override fun onResume() {
        super.onResume()
        if (config.lockWhenLeavingApp == LockWhenLeavingApp.ENABLE && !config.isShowLock) {
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
        initScreenOffAction()
        setupNavigationDrawer()
        setSupportActionBar(binding.toolbar)
        setupActionBar()
        requestAllFileManage()

    }


    private fun createSecretFolderFirstTime() {
        if (!AppSharePreference.INSTANCE.getInitDone(false)) {
            val callback = object : ICreateFile {
                override fun onSuccess() {

                }

                override fun onFailed() {

                }
            }
            viewModel.createFolder(filesDir, Constant.PICTURE_FOLDER_NAME, callback)
            viewModel.createFolder(filesDir, Constant.VIDEOS_FOLDER_NAME, callback)
            viewModel.createFolder(filesDir, Constant.AUDIOS_FOLDER_NAME, callback)
            viewModel.createFolder(filesDir, Constant.FILES_FOLDER_NAME, callback)
            viewModel.createFolder(filesDir, Constant.RECYCLER_BIN_FOLDER_NAME, callback)
            AppSharePreference.INSTANCE.saveInitFirstDone(true)
            viewModel.getListFolderInVault(this,filesDir)

        }
    }

    private fun requestAllFileManage() {
        if (buildMinVersionS()) {
            val hasManageExternalStoragePermission = Environment.isExternalStorageManager()

            if (hasManageExternalStoragePermission) {
                createSecretFolderFirstTime()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse("package:" + packageName)
                launcher.launch(intent)
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
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
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
            createSecretFolderFirstTime()
        }


     fun setupActionBar() {
        val navController: NavController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
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

    fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun onSupportNavigateUp(): Boolean {
        return (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        removeScreenOffAction()
        super.onDestroy()
    }


}