package com.neko.hiepdph.calculatorvault.ui.main.home.setting.disguiseicon

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.config.ButtonToUnlock
import com.neko.hiepdph.calculatorvault.config.HideAppIcon
import com.neko.hiepdph.calculatorvault.databinding.FragmentDisguiseIconBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogButtonToUnlock
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentDisguiseIcon : Fragment() {
    private var _binding: FragmentDisguiseIconBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisguiseIconBinding.inflate(inflater, container, false)
        AppSharePreference.INSTANCE.registerOnSharedPreferenceChangeListener(listener)
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

    private val listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == Constant.KEY_BUTTON_UNLOCK) {
            binding.containerPressButtonToUnlock.tvStatus.text =
                when (requireContext().config.buttonToUnlock) {
                    ButtonToUnlock.SHORT_PRESS -> getString(R.string.short_press)
                    ButtonToUnlock.LONG_PRESS -> getString(R.string.long_press)
                    else -> getString(R.string.none)
                }
        }
    }

    private fun initButton() {
        binding.containerHideAppIcon.root.clickWithDebounce {
            if (!requireContext().config.hideAppIcon) {
                val confirmDialog = DialogConfirm(onPositiveClicked = {
                    navigateToPage(R.id.fragmentDisguiseIcon, R.id.fragmentHideAppIcon)
                }, DialogConfirmType.TIP_HIDE_APP)
                confirmDialog.show(childFragmentManager, confirmDialog.tag)
            } else {
                requireContext().config.hideAppIcon = HideAppIcon.OFF
            }
        }

        binding.containerUnlockAfterDialing.root.clickWithDebounce {
            binding.containerUnlockAfterDialing.switchChange.isChecked =
                !binding.containerUnlockAfterDialing.switchChange.isChecked
            requireContext().config.unlockAfterDialing =
                binding.containerUnlockAfterDialing.switchChange.isChecked
        }

        binding.containerUnlockAfterDialing.switchChange.setOnClickListener {
            requireContext().config.unlockAfterDialing =
                binding.containerUnlockAfterDialing.switchChange.isChecked
        }

        binding.containerChangeCalculatorIcon.root.clickWithDebounce {
            binding.containerChangeCalculatorIcon.imvAppIcon
        }

        binding.containerPressButtonToUnlock.root.clickWithDebounce {
            val dialogButtonToUnlock = DialogButtonToUnlock()
            dialogButtonToUnlock.show(childFragmentManager, dialogButtonToUnlock.tag)
        }

        binding.containerUnlockByLongPressTitle.root.clickWithDebounce {
            binding.containerUnlockByLongPressTitle.switchChange.isChecked =
                !binding.containerUnlockByLongPressTitle.switchChange.isChecked
            requireContext().config.prohibitUnlockingByLongPressTitle =
                binding.containerUnlockByLongPressTitle.switchChange.isChecked
        }

        binding.containerUnlockByLongPressTitle.switchChange.setOnClickListener {
            requireContext().config.prohibitUnlockingByLongPressTitle =
                binding.containerUnlockByLongPressTitle.switchChange.isChecked
        }

        binding.containerUnlockByFingerprint.root.clickWithDebounce {
            binding.containerUnlockByFingerprint.switchChange.isChecked =
                !binding.containerUnlockByFingerprint.switchChange.isChecked
            requireContext().config.unlockByFingerprint =
                binding.containerUnlockByFingerprint.switchChange.isChecked
        }
        binding.containerUnlockByFingerprint.switchChange.setOnClickListener {
            requireContext().config.unlockByFingerprint =
                binding.containerUnlockByFingerprint.switchChange.isChecked
        }

        binding.containerUnlockFingerprintFailure.root.clickWithDebounce {
            binding.containerUnlockFingerprintFailure.switchChange.isChecked =
                !binding.containerUnlockFingerprintFailure.switchChange.isChecked
            requireContext().config.fingerprintFailure =
                binding.containerUnlockFingerprintFailure.switchChange.isChecked
        }

        binding.containerUnlockFingerprintFailure.switchChange.setOnClickListener {
            requireContext().config.fingerprintFailure =
                binding.containerUnlockFingerprintFailure.switchChange.isChecked
        }

    }

    private fun setupView() {
        binding.containerHideAppIcon.apply {
            imvIcon.setImageResource(R.drawable.ic_hide_app)
            tvContent.text = getString(R.string.hide_app_icon)
            tvStatus.show()
            tvStatus.text =
                if (requireContext().config.hideAppIcon) getString(R.string.on) else getString(R.string.off)
        }
        binding.containerUnlockAfterDialing.apply {
            imvIcon.setImageResource(R.drawable.ic_setup_pin)
            tvContent.text = getString(R.string.need_to_unlock_after_dialing)
            tvContent2.text = getString(R.string.unlock_after_dialing_content_2)
            switchChange.show()
            switchChange.isChecked = requireContext().config.unlockAfterDialing
            imvNext.hide()
        }
        binding.containerChangeCalculatorIcon.apply {
            imvIcon.setImageResource(R.drawable.ic_calculator_dark)
            tvContent.text = getString(R.string.change_calculator_icon)
            tvContent2.text = getString(R.string.change_caculator_icon_content_2)
            imvAppIcon.setImageResource(R.drawable.ic_lock_safe)
        }
        binding.containerPressButtonToUnlock.apply {
            imvIcon.setImageResource(R.drawable.ic_unlock)
            tvContent.text = getString(R.string.press_button_to_unlock)
            tvContent2.text = getString(R.string.press_button_to_unlock_content_2)
            tvStatus.show()
            tvStatus.text = when (requireContext().config.buttonToUnlock) {
                ButtonToUnlock.SHORT_PRESS -> getString(R.string.short_press)
                ButtonToUnlock.LONG_PRESS -> getString(R.string.long_press)
                else -> getString(R.string.none)
            }
        }
        binding.containerUnlockByLongPressTitle.apply {
            imvIcon.setImageResource(R.drawable.ic_lock_safe)
            tvContent.text = getString(R.string.prohibit_unlocking_by_long_press)
            tvContent2.text = getString(R.string.prohibit_unlocking_by_long_press_content_2)
            tvStatus.hide()
            switchChange.show()
            imvNext.hide()
            switchChange.isChecked = requireContext().config.prohibitUnlockingByLongPressTitle
        }
        binding.containerUnlockByFingerprint.apply {
            imvIcon.setImageResource(R.drawable.ic_fingerprint_lock_display)
            tvContent.text = getString(R.string.unlock_by_fingerprint)
            tvContent2.text = getString(R.string.unlock_by_fingerprint_content_2)
            imvNext.hide()
            switchChange.show()
            switchChange.isChecked = requireContext().config.unlockByFingerprint
        }
        binding.containerUnlockFingerprintFailure.apply {
            imvIcon.setImageResource(R.drawable.ic_fingerprint_failure)
            tvContent.text = getString(R.string.fingerprint_failure)
            tvContent2.text = getString(R.string.fingerprint_failure_content_2)
            imvNext.hide()
            switchChange.show()
            switchChange.isChecked = requireContext().config.fingerprintFailure
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppSharePreference.INSTANCE.unregisterListener(listener)
    }

}