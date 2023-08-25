package com.neko.hiepdph.calculatorvault.ui.main.home.setting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.neko.hiepdph.calculatorvault.CustomApplication
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.utils.buildMinVersionO
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.databinding.FragmentSettingBinding
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityCamera
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSetting : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private var action: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initToolBar()
        changeBackPressCallBack {
            requireActivity().finishAffinity()
        }
    }

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }


    private fun initView() {
        setupView()
        val groupData = mutableListOf(
            Pair(R.drawable.ic_setting_general, getString(R.string.general)),
            Pair(R.drawable.ic_setting_safe, getString(R.string.safe)),
            Pair(R.drawable.ic_setting_disguise, getString(R.string.disguise_icon)),
            Pair(R.drawable.ic_setting_private_camera, getString(R.string.private_camera)),
            Pair(R.drawable.ic_setting_advanced, getString(R.string.advanced)),
            Pair(R.drawable.ic_retrieve_lost_file, getString(R.string.retrive_lost_files)),
            Pair(R.drawable.ic_setting_back_up, getString(R.string.backup)),
            Pair(R.drawable.ic_device_migration, getString(R.string.device_migration)),
            Pair(R.drawable.ic_setting_clear_cache, getString(R.string.clear_cache)),
//            Pair(
//                R.drawable.ic_setting_prevent_lost_file_help,
//                getString(R.string.preventing_lost_files_help)
//            ),
//            Pair(R.drawable.ic_setting_faq, getString(R.string.FAQ)),
        )

        val groupView = mutableListOf(
            binding.itemGeneral,
            binding.itemSafe,
            binding.itemDisguiseIcon,
            binding.itemPrivateCamera,
            binding.itemAdvanced,
            binding.itemRetrieveLostFiles,
            binding.itemBackup,
            binding.itemDeviceMigration,
//            binding.itemLanguage,
            binding.itemClearCache,
//            binding.itemPreventingLostFileHelp,
//            binding.itemFaq,
        )

        groupView.forEachIndexed { index, item ->
            item.imvIcon.setBackgroundResource(groupData[index].first)
            item.tvContent.text = groupData[index].second
            if (item == binding.itemAdvanced
                || item == binding.itemDeviceMigration
                || item == binding.itemClearCache
//                || item == binding.itemFaq
              ) {
                item.line.hide()
            }
            if(item == binding.itemRetrieveLostFiles || item == binding.itemBackup || item == binding.itemDeviceMigration ){
                item.tvContent.setTextColor(getColor(R.color.neutral_10))
                item.imvNext.setColorFilter(getColor(R.color.neutral_10))
            }
        }
        initButton()
    }

    private fun setupView() {
        if (buildMinVersionO()) {
            binding.itemPrivateCamera.root.show()
        } else {
            binding.itemPrivateCamera.root.hide()
        }
        binding.itemClearCache.tvStatus.show()
        binding.itemClearCache.tvStatus.text =
            requireContext().cacheDir.walkBottomUp().fold(0L) { acc, file -> acc + file.length() }
                .formatSize()
    }

    private fun clearCache() {
        requireContext().cacheDir.deleteRecursively()
        binding.itemClearCache.tvStatus.text =
            requireContext().cacheDir.walkBottomUp().fold(0L) { acc, file -> acc + file.length() }
                .formatSize()
    }

    private fun initButton() {
        binding.itemSafe.root.clickWithDebounce {
            navigateToPage(R.id.fragmentSetting, R.id.fragmentSafe)
        }
        binding.itemPrivateCamera.root.clickWithDebounce {
            if (buildMinVersionO()) {
                checkPermission(action = {
                    createShortcutsCamera()
                })
            }
        }
        binding.itemGeneral.root.clickWithDebounce {
            navigateToPage(R.id.fragmentSetting, R.id.fragmentGeneral)
        }
        binding.itemDisguiseIcon.root.clickWithDebounce {
            navigateToPage(R.id.fragmentSetting, R.id.fragmentDisguiseIcon)

        }

        binding.itemAdvanced.root.clickWithDebounce {
            navigateToPage(R.id.fragmentSetting, R.id.fragmentAdvance)
        }

        binding.itemClearCache.root.clickWithDebounce {
            clearCache()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createShortcutsCamera() {
        val shortcutManager = requireActivity().getSystemService(ShortcutManager::class.java)
        val intent = Intent(requireContext(), ActivityCamera::class.java)
        intent.action = Intent.ACTION_VIEW
        if (shortcutManager.isRequestPinShortcutSupported) {
            val shortcut = ShortcutInfo.Builder(requireContext(), getString(R.string.camera))
                .setShortLabel(getString(R.string.camera)).setIcon(
                    Icon.createWithResource(
                        requireContext(), R.drawable.ic_setting_private_camera
                    )
                ).setIntent(intent).build()

            shortcutManager.requestPinShortcut(shortcut, null)
        }
    }

    private fun checkPermission(action: () -> Unit) {
        this.action = action
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.CAMERA
                )
            ) {
                checkCameraActivityLauncher(
                    action, Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)
                    )
                )
            } else {
                CustomApplication.app.resumeFromApp = true
                launcher.launch(Manifest.permission.CAMERA)

            }
        } else {
            action()
        }
    }


    private fun checkCameraActivityLauncher(action: () -> Unit, intent: Intent) {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                action()
            }
        }.launch(intent)
    }


    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            action?.invoke()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}