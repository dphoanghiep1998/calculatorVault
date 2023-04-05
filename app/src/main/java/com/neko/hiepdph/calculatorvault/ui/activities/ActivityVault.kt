package com.neko.hiepdph.calculatorvault.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.databinding.ActivityVaultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityVault : AppCompatActivity() {
    private lateinit var binding: ActivityVaultBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigationDrawer()

        setSupportActionBar(binding.toolbar)

        val navController: NavController = findNavController(R.id.nav_host_fragment)
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

    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout.apply {
            setStatusBarBackground(R.color.purple_200)
        }
    }

    fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}