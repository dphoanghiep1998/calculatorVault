package com.neko.hiepdph.calculatorvault.ui.main.home.setting.disguiseicon.hideappicon

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.neko.hiepdph.calculatorvault.BuildConfig
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant.listGroup
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.databinding.FragmentHideAppIconBinding
import com.neko.hiepdph.calculatorvault.hideapp.HideAppIcon
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
    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
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
            if (requireActivity().config.hideAppIcon) {
                hideAppIcon()
            } else {
                showAppIcon()
            }
        }

        binding.containerHideAppIcon.switchChange.setOnClickListener {
            requireContext().config.hideAppIcon =
                binding.containerHideAppIcon.switchChange.isChecked
            if (requireActivity().config.hideAppIcon) {
                hideAppIcon()
            } else {
                showAppIcon()
            }
        }
    }

    private fun hideAppIcon() {
        HideAppIcon.Builder(requireActivity() as ActivityVault)
            .activeName(listGroup[requireContext().config.changeCalculatorIcon])
            .packageName(BuildConfig.APPLICATION_ID).build().hideIcon()
    }

    private fun showAppIcon() {
        HideAppIcon.Builder(requireActivity() as ActivityVault)
            .activeName(listGroup[requireContext().config.changeCalculatorIcon])
            .packageName(BuildConfig.APPLICATION_ID).build().showIcon()
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