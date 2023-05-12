package com.neko.hiepdph.calculatorvault.ui.main.home.setting

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack {
            requireActivity().finishAffinity()
        }
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
            Pair(R.drawable.ic_setting_language, getString(R.string.change_language)),
            Pair(R.drawable.ic_setting_clear_cache, getString(R.string.clear_cache)),
            Pair(
                R.drawable.ic_setting_prevent_lost_file_help,
                getString(R.string.preventing_lost_files_help)
            ),
            Pair(R.drawable.ic_setting_faq, getString(R.string.FAQ)),
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
            binding.itemLanguage,
            binding.itemClearCache,
            binding.itemPreventingLostFileHelp,
            binding.itemFaq,
        )

        groupView.forEachIndexed { index, item ->
            item.imvIcon.setBackgroundResource(groupData[index].first)
            item.tvContent.text = groupData[index].second
            if (item == binding.itemAdvanced || item == binding.itemDeviceMigration || item == binding.itemLanguage || item == binding.itemFaq) {
                item.line.hide()
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
                createShortcutsCamera()
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

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}