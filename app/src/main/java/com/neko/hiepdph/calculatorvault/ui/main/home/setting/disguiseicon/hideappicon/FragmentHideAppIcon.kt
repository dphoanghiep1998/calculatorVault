package com.neko.hiepdph.calculatorvault.ui.main.home.setting.disguiseicon.hideappicon

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.databinding.FragmentHideAppIconBinding
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentHideAppIcon : Fragment() {

    private var _binding: FragmentHideAppIconBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHideAppIconBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setupView()
        initButton()
    }

    private fun initButton() {
        binding.containerHideAppIcon.root.clickWithDebounce {
            binding.containerHideAppIcon.switchChange.isChecked =
                !binding.containerHideAppIcon.switchChange.isChecked
            requireContext().config.hideAppIcon =
                binding.containerHideAppIcon.switchChange.isChecked
            if(requireActivity().config.hideAppIcon){
                hideAppIcon()
            }else{
                showAppIcon()
            }
        }

        binding.containerHideAppIcon.switchChange.setOnClickListener {
            requireContext().config.hideAppIcon =
                binding.containerHideAppIcon.switchChange.isChecked
            if(requireActivity().config.hideAppIcon){
                hideAppIcon()
            }else{
                showAppIcon()
            }
        }
    }

    private fun hideAppIcon() {
        val p: PackageManager = requireActivity().packageManager
        val componentName = ComponentName(
            requireActivity(), ActivityVault::class.java
        )
        p.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        );
    }

    private fun showAppIcon() {
        val p: PackageManager = requireActivity().packageManager
        val componentName = ComponentName(requireActivity(), ActivityVault::class.java)
        p.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun setupView() {
        binding.containerHideAppIcon.apply {
            tvContent.text = getString(R.string.hide_app_icon)
            switchChange.show()
            switchChange.isChecked = requireContext().config.hideAppIcon
            imvNext.hide()
            imvIcon.hide()
            line.hide()
        }
    }


}