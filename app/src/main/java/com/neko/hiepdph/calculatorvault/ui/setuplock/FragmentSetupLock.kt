package com.neko.hiepdph.calculatorvault.ui.setuplock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.databinding.FragmentSetupLockBinding


class FragmentSetupLock : Fragment() {
    private var _binding: FragmentSetupLockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupLockBinding.inflate(inflater, container, false)
        initView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
        initDropdown()
        initData()
    }

    private fun initData() {
        binding.edtAnswer.setText(requireContext().config.securityAnswer)
        if (requireContext().config.securityQuestion != String.EMPTY) {
            binding.question.setText(requireContext().config.securityQuestion)
        } else {
            binding.question.setText(getString(R.string.lock_ask_1))
        }
        binding.edtAnswer.setText(requireContext().config.securityAnswer)
    }

    private fun initDropdown() {
        binding.question.dismissDropDown()

        val arrayQuestion = arrayOf(
            getString(R.string.lock_ask_1),
            getString(R.string.lock_ask_2),
            getString(R.string.lock_ask_3),
            getString(R.string.lock_ask_4),
        )
        binding.question.setSimpleItems(arrayQuestion)


        binding.btnConfim.clickWithDebounce {
            requireContext().config.securityQuestion = binding.question.text.toString()
            requireContext().config.securityAnswer = binding.edtAnswer.text.toString()
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}