package com.neko.hiepdph.calculatorvault.ui.setuplock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.popBackStack
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.databinding.FragmentSetupLockBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentSetupLock : Fragment() {
    private var _binding: FragmentSetupLockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupLockBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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
        lifecycleScope.launchWhenResumed {
            binding.question.setSimpleItems(arrayQuestion)
        }


        binding.btnConfim.clickWithDebounce {
            requireContext().config.securityQuestion = binding.question.text.toString()
            requireContext().config.securityAnswer = binding.edtAnswer.text.toString()
            popBackStack()
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}