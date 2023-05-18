package com.neko.hiepdph.calculatorvault.ui.main.home.setting.safe.setuplock

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.popBackStack
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.databinding.FragmentQuestionLockBinding
import com.neko.hiepdph.calculatorvault.databinding.FragmentSetupLockBinding


class FragmentQuestionLock : Fragment() {
    private var _binding: FragmentQuestionLockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionLockBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initToolBar()
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
            popBackStack(R.id.fragmentSetupLock)
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}