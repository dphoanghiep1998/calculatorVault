package com.neko.hiepdph.calculatorvault.ui.main.home.setting

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.databinding.FragmentSettingBinding


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
    }


    private fun initView() {
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
            item.imvItem.setBackgroundResource(groupData[index].first)
            item.tvTitle.text = groupData[index].second
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}