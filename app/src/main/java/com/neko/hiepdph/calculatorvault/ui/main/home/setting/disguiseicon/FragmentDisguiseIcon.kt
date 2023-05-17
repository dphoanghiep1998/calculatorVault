package com.neko.hiepdph.calculatorvault.ui.main.home.setting.disguiseicon

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.neko.hiepdph.calculatorvault.BuildConfig
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.changeicon.AppIconNameChanger
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.buildMinVersionQ
import com.neko.hiepdph.calculatorvault.config.ButtonToUnlock
import com.neko.hiepdph.calculatorvault.config.HideAppIcon
import com.neko.hiepdph.calculatorvault.databinding.FragmentDisguiseIconBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutListIconBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogButtonToUnlock
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentDisguiseIcon : Fragment() {
    private var _binding: FragmentDisguiseIconBinding? = null
    private val binding get() = _binding!!
    private lateinit var popupWindow: PopupWindow

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDisguiseIconBinding.inflate(inflater, container, false)
        AppSharePreference.INSTANCE.registerOnSharedPreferenceChangeListener(listener)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        initPopupWindow()
        initView()
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
        if (key == Constant.KEY_CHANGE_ICON) {
            binding.containerChangeCalculatorIcon.imvAppIcon.setImageResource(Constant.listIcon[requireContext().config.changeCalculatorIcon])
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
            popupWindow.showAsDropDown(binding.containerChangeCalculatorIcon.imvNext)
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
        if (buildMinVersionQ()) {
            binding.containerHideAppIcon.root.show()
        } else {
            binding.containerHideAppIcon.root.hide()
        }
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
            imvAppIcon.setImageResource(Constant.listIcon[requireContext().config.changeCalculatorIcon])
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

    private fun initPopupWindow() {
        val inflater: LayoutInflater =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
        val bindingLayout = LayoutListIconBinding.inflate(inflater, null, false)



        popupWindow = PopupWindow(
            bindingLayout.root,
            (Resources.getSystem().displayMetrics.widthPixels * 0.6).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val listGroup = mutableListOf(
            Pair(bindingLayout.icon1, "com.neko.hiepdph.calculatorvault.alias1"),
            Pair(bindingLayout.icon2, "com.neko.hiepdph.calculatorvault.alias2"),
            Pair(bindingLayout.icon3, "com.neko.hiepdph.calculatorvault.alias3"),
            Pair(bindingLayout.icon4, "com.neko.hiepdph.calculatorvault.alias4"),
            Pair(bindingLayout.icon5, "com.neko.hiepdph.calculatorvault.alias5"),
            Pair(bindingLayout.icon6, "com.neko.hiepdph.calculatorvault.alias6"),
            Pair(bindingLayout.icon7, "com.neko.hiepdph.calculatorvault.alias7"),
            Pair(bindingLayout.icon8, "com.neko.hiepdph.calculatorvault.alias8"),
            Pair(bindingLayout.icon9, "com.neko.hiepdph.calculatorvault.alias9"),
            Pair(bindingLayout.icon10, "com.neko.hiepdph.calculatorvault.alias10"),
            Pair(bindingLayout.icon11, "com.neko.hiepdph.calculatorvault.alias11"),
            Pair(bindingLayout.icon12, "com.neko.hiepdph.calculatorvault.alias12"),
        )

        listGroup.forEachIndexed { index, item ->
            item.first.clickWithDebounce {
                requireContext().config.changeCalculatorIcon = index
                val listDisableNames = listGroup.filter { it.second != item.second }.map { it.second }
                setAppIcon(item.second,listDisableNames)
                popupWindow.dismiss()

            }
        }

    }

    private fun setAppIcon(activeName: String, disableNames: List<String>) {
        AppIconNameChanger.Builder(requireActivity() as ActivityVault).activeName(activeName)
            .disableNames(disableNames).packageName(BuildConfig.APPLICATION_ID).build().setNow()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppSharePreference.INSTANCE.unregisterListener(listener)
    }

}