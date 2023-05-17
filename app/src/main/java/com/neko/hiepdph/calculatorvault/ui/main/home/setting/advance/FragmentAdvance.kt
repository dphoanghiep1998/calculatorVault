package com.neko.hiepdph.calculatorvault.ui.main.home.setting.advance

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.databinding.FragmentAdvanceBinding


class FragmentAdvance : Fragment() {

    private lateinit var binding: FragmentAdvanceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdvanceBinding.inflate(inflater, container, false)
        initView()
        initToolBar()
        AppSharePreference.INSTANCE.registerOnSharedPreferenceChangeListener(listener)
        return binding.root
    }


    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == Constant.KEY_PROHIBIT_SCREENSHOT) {
                if (requireContext().config.prohibitScreenShot) {
                    blockScreenShot()
                } else {
                    clearFlag()
                }
            }
        }


    private fun initView() {
        setupView()
        initAction()
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
    private fun initAction() {
        binding.itemIntruderSelfie.root.clickWithDebounce {
            navigateToPage(R.id.fragmentAdvance, R.id.fragmentIntruder)
        }

        binding.itemProhibitScreenshots.root.clickWithDebounce {
            binding.itemProhibitScreenshots.switchChange.isChecked =
                !binding.itemProhibitScreenshots.switchChange.isChecked
            requireContext().config.prohibitScreenShot =
                binding.itemProhibitScreenshots.switchChange.isChecked
        }
        binding.itemProhibitScreenshots.switchChange.setOnClickListener {
            requireContext().config.prohibitScreenShot =
                binding.itemProhibitScreenshots.switchChange.isChecked
        }

        binding.itemFakePassword.root.clickWithDebounce {
            binding.itemFakePassword.switchChange.isChecked =
                !binding.itemFakePassword.switchChange.isChecked
            requireContext().config.fakePassword = binding.itemFakePassword.switchChange.isChecked
        }
        binding.itemFakePassword.switchChange.setOnClickListener {
            requireContext().config.fakePassword = binding.itemFakePassword.switchChange.isChecked
        }


    }


    private fun setupView() {
        binding.itemFakePassword.apply {
            switchChange.isChecked = requireContext().config.fakePassword

            imvIcon.setImageResource(R.drawable.ic_fake_password_light)
            tvContent.text = getString(R.string.fake_password)
            switchChange.show()
            imvNext.hide()
            tvContent2.text = getString(R.string.fake_password_content_2)
        }

        binding.itemIntruderSelfie.apply {
            imvIcon.setImageResource(R.drawable.ic_intruder_light)
            tvContent.text = getString(R.string.intruder_selfie)
            tvContent2.text = getString(R.string.intruder_content_2)
            tvStatus.text = if (requireContext().config.photoIntruder) {
                getString(R.string.on)
            } else {
                getString(R.string.off)
            }
        }

        binding.itemProhibitScreenshots.apply {
            switchChange.isChecked = requireContext().config.prohibitScreenShot

            imvIcon.setImageResource(R.drawable.ic_screenshot_prevent_light)
            tvContent.text = getString(R.string.prohibit_screenshot)
            switchChange.show()
            imvNext.hide()
            tvContent2.text = getString(R.string.prohibit_screenshot_content_2)
        }
    }

    private fun blockScreenShot() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )
        val window: Window = requireActivity().window
        val wm = requireActivity().windowManager
        wm.removeViewImmediate(window.decorView)
        wm.addView(window.decorView, window.attributes)
    }

    private fun clearFlag() {
        requireActivity().window.clearFlags(
            WindowManager.LayoutParams.FLAG_SECURE
        )
        val window: Window = requireActivity().window
        val wm = requireActivity().windowManager
        wm.removeViewImmediate(window.decorView)
        wm.addView(window.decorView, window.attributes)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppSharePreference.INSTANCE.unregisterListener(listener)
    }


}