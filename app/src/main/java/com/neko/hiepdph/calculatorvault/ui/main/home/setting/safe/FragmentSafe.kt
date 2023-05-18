package com.neko.hiepdph.calculatorvault.ui.main.home.setting.safe

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.config.ScreenOffAction
import com.neko.hiepdph.calculatorvault.databinding.FragmentSafeBinding

class FragmentSafe : Fragment() {
    private var _binding: FragmentSafeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSafeBinding.inflate(
            inflater, container, false
        )
        AppSharePreference.INSTANCE.registerOnSharedPreferenceChangeListener(listener)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
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
        lifecycleScope.launchWhenResumed {
            setupView()
        }
        initButton()
    }

    private fun initButton() {
        binding.containerLock.root.clickWithDebounce {
            findNavController().navigate(R.id.fragmentLock)
        }
        binding.containerScreenOffAction.root.clickWithDebounce {
            findNavController().navigate(R.id.dialogChangeScreenOffAction)
        }

        binding.containerFingerprintUnlock.root.clickWithDebounce {
            binding.containerFingerprintUnlock.switchChange.isChecked = !binding.containerFingerprintUnlock.switchChange.isChecked
            requireContext().config.fingerPrintUnlock =
                binding.containerFingerprintUnlock.switchChange.isChecked
        }
        binding.containerFingerprintUnlock.switchChange.setOnClickListener {
            requireContext().config.fingerPrintUnlock =
                binding.containerFingerprintUnlock.switchChange.isChecked
        }
        binding.containerFingerprintLockDisplay.root.clickWithDebounce {
            binding.containerFingerprintLockDisplay.switchChange.isChecked = !binding.containerFingerprintLockDisplay.switchChange.isChecked
            requireContext().config.fingerPrintLockDisplay =
                binding.containerFingerprintLockDisplay.switchChange.isChecked
        }
        binding.containerFingerprintLockDisplay.switchChange.setOnClickListener {
            requireContext().config.fingerPrintLockDisplay =
                binding.containerFingerprintLockDisplay.switchChange.isChecked
        }

        binding.containerLockWhenLeaving.root.clickWithDebounce {
            binding.containerLockWhenLeaving.switchChange.isChecked = !binding.containerLockWhenLeaving.switchChange.isChecked
            requireContext().config.lockWhenLeavingApp =
                binding.containerLockWhenLeaving.switchChange.isChecked
        }
        binding.containerLockWhenLeaving.switchChange.setOnClickListener {
            requireContext().config.lockWhenLeavingApp =
                binding.containerLockWhenLeaving.switchChange.isChecked
        }
        binding.containerPasswordRecoveryQuestion.root.clickWithDebounce {
            findNavController().navigate(R.id.fragmentQuestionLock)
        }
    }

    private fun setupView() {
        binding.containerLock.apply {
            imvIcon.setImageResource(R.drawable.ic_lock_safe)
            tvContent.text = getString(R.string.lock)
        }
        binding.containerPasswordRecoveryQuestion.apply {
            imvIcon.setImageResource(R.drawable.ic_password_recovery_safe)
            tvContent.text = getString(R.string.password_recovery_question)
        }
        binding.containerFingerprintUnlock.apply {
            imvIcon.setImageResource(R.drawable.ic_fingerprint_unlock)
            tvContent.text = getString(R.string.fingerprint_unlock)
            tvContent2.text = getString(R.string.unlock_app_with_registered_fingerprint)
            imvNext.hide()
            switchChange.show()
            switchChange.isChecked = requireContext().config.fingerPrintUnlock
        }
        binding.containerFingerprintLockDisplay.apply {
            imvIcon.setImageResource(R.drawable.ic_fingerprint_lock_display)
            tvContent.text = getString(R.string.fingerprint_lock_display)
            tvContent2.text = getString(R.string.unlock_interface_displays_fingerprint_pattern)
            imvNext.hide()
            switchChange.show()
            switchChange.isChecked = requireContext().config.fingerPrintLockDisplay

        }
        binding.containerScreenOffAction.apply {
            imvIcon.setImageResource(R.drawable.ic_screen_off_action)
            tvContent.text = getString(R.string.screen_off_action)
            tvContent2.text = getString(R.string.content_2_screen_off_action)
            tvStatus.text = getScreenOffAction(requireContext().config.screenOffAction)
            tvStatus.show()
        }
        binding.containerLockWhenLeaving.apply {
            imvIcon.setImageResource(R.drawable.ic_lock_when_leaving)
            tvContent.text = getString(R.string.lock_when_leaving_app)
            tvContent2.text = getString(R.string.content_2_lock_when_leaving_app)
            imvNext.hide()
            switchChange.show()
            switchChange.isChecked = requireContext().config.lockWhenLeavingApp
        }
//        binding.containerTemporaryFileDeletionTime.apply {
//            imvIcon.setImageResource(R.drawable.ic_delete_temporary)
//            tvContent.text = getString(R.string.temporary_file_deletion_time)
//            tvContent2.text = getString(R.string.content_2_temporary_file_deletion_time)
//        }
    }

    private fun getScreenOffAction(values: Int): String {
        return when (values) {
            ScreenOffAction.NOACTION -> getString(R.string.no_action)
            ScreenOffAction.GOTOHOMESCREEN -> getString(R.string.go_to_homescreen)
            ScreenOffAction.LOCKAGAIN -> getString(R.string.lock_again)
            else -> getString(R.string.no_action)
        }
    }


    override fun onDestroy() {
        AppSharePreference.INSTANCE.unregisterListener(listener)
        super.onDestroy()
    }

    private val listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == Constant.KEY_SCREEN_OFF_ACTION) {
            binding.containerScreenOffAction.tvStatus.text =
                getScreenOffAction(requireContext().config.screenOffAction)
        }
    }


}