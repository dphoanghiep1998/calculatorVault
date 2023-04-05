package com.neko.hiepdph.calculatorvault.ui.setuplock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.databinding.FragmentSetupLockBinding


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
    }

    private fun initDropdown() {
        val arrayQuestion = arrayOf(
            getString(R.string.lock_ask_2),
            getString(R.string.lock_ask_1),
            getString(R.string.lock_ask_3),
            getString(R.string.lock_ask_4),
            )
        binding.question.setSimpleItems(arrayQuestion)

        binding.question.dismissDropDown()
        binding.question.setOnItemClickListener { parent, view, position, id ->

        }

        binding.btnConfim.clickWithDebounce {
            AppSharePreference.INSTANCE.setSecurityQuestion(binding.question.text.toString())
            AppSharePreference.INSTANCE.setSecurityAnswer(binding.edtAnswer.text.toString())
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}