package com.neko.hiepdph.calculatorvault.ui.main.home.setting.advance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.databinding.FragmentAdvanceBinding

class FragmentAdvance : Fragment() {

    private lateinit var binding: FragmentAdvanceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdvanceBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        setupView()
        initAction()
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
            requireContext().config.fakePassword =
                binding.itemFakePassword.switchChange.isChecked
        }
        binding.itemFakePassword.switchChange.setOnClickListener {
            requireContext().config.fakePassword =
                binding.itemFakePassword.switchChange.isChecked
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


}