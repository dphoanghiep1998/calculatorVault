package com.neko.hiepdph.calculatorvault.ui.main.home.setting.safe.lock

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.config.LockType
import com.neko.hiepdph.calculatorvault.databinding.FragmentLockBinding


class FragmentLock : Fragment() {
    private var _binding: FragmentLockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLockBinding.inflate(inflater, container, false)
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

    private fun initButton() {
        binding.containerLockType.root.clickWithDebounce {
            findNavController().navigate(R.id.dialogChangeLockType)
        }
        binding.containerVisiblePattern.switchChange.setOnClickListener {
            requireContext().config.visiblePattern = binding.containerVisiblePattern.switchChange.isChecked
        }
        binding.containerTactileFeedback.switchChange.setOnClickListener {
            requireContext().config.tactileFeedback = binding.containerTactileFeedback.switchChange.isChecked
        }
    }

    private fun setupView() {
        binding.containerLockType.apply {
            imvIcon.hide()
            tvContent.text = getString(R.string.lock_type)
            tvStatus.show()
            tvStatus.text = getPattern(requireContext().config.lockType)
        }
        binding.containerTactileFeedback.apply {
            imvIcon.hide()
            tvContent.text = getString(R.string.tactile_feedback)
            switchChange.show()
            imvNext.hide()
            switchChange.isChecked = requireContext().config.tactileFeedback
        }
        binding.containerVisiblePattern.apply {
            imvIcon.hide()
            tvContent.text = getString(R.string.visible_pattern)
            switchChange.show()
            imvNext.hide()
            switchChange.isChecked = requireContext().config.visiblePattern

        }
        binding.containerChangeUnlock.apply {
            imvIcon.hide()
            tvContent.text = getString(R.string.change_unlock)
            tvStatus.hide()
        }
    }

    private fun getPattern(value: Int): String {
        return when (value) {
            LockType.PATTERN -> getString(R.string.pattern)
            LockType.PIN -> getString(R.string.pin)
            LockType.NONE -> getString(R.string.none)
            else -> getString(R.string.none)
        }
    }

    override fun onDestroy() {
        AppSharePreference.INSTANCE.unregisterListener(listener)
        super.onDestroy()
    }


    private val listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == Constant.KEY_LOCK_TYPE) {
            binding.containerLockType.tvStatus.text = getPattern(requireContext().config.lockType)
        }
    }


}