package com.neko.hiepdph.calculatorvault.ui.main.home.setting.safe.lock.changepin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.customview.PinFunction
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.popBackStack
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.databinding.FragmentChangePinBinding
import com.neko.hiepdph.calculatorvault.viewmodel.ChangePinViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentChangePin : Fragment() {
    private var _binding: FragmentChangePinBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ChangePinViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePinBinding.inflate(inflater, container, false)
        initView()
        observeState()
        return binding.root
    }

    private fun observeState() {
        viewModel.currentState.observe(viewLifecycleOwner) {
            it?.let {
                changeViewByState(it)
            }

        }
    }

    private fun changeViewByState(state: Int) {
        when (state) {
            0 -> {
                binding.tvTitle.setText(R.string.enter_original_password)
            }
            1 -> {
                binding.tvTitle.setText(R.string.setup_a_new_password)
            }
            else -> {
                binding.tvTitle.setText(R.string.confirm_your_password)
            }
        }
    }

    private fun initView() {
        binding.pinView.setCalculatorMainCallback(object : PinFunction {
            override fun onPressButton0() {
                appendToPassword("0")
            }

            override fun onPressButton1() {
                appendToPassword("1")
            }

            override fun onPressButton2() {
                appendToPassword("2")
            }

            override fun onPressButton3() {
                appendToPassword("3")
            }

            override fun onPressButton4() {
                appendToPassword("4")
            }

            override fun onPressButton5() {
                appendToPassword("5")
            }

            override fun onPressButton6() {
                appendToPassword("6")
            }

            override fun onPressButton7() {
                appendToPassword("7")
            }

            override fun onPressButton8() {
                appendToPassword("8")
            }

            override fun onPressButton9() {
                appendToPassword("9")
            }

            override fun onPressButtonCheck() {
                checkPassword()
            }

            override fun onPressButtonBackspace() {
                handleBackSpace()
            }

        })
    }

    private fun checkPassword() {
        when (viewModel.currentState.value) {
            0 -> {
                val passwordLv1 = viewModel.state1Password.value
                passwordLv1?.let { pw ->
                    if (pw.length < 4) {
                        showSnackBar(
                            getString(R.string.password_must_be_4_characters), SnackBarType.FAILED
                        )
                        return
                    }
                    if (pw != requireContext().config.secretPin) {
                        showSnackBar(
                            getString(R.string.current_password_is_not_correct), SnackBarType.FAILED
                        )
                        return
                    }
                    if (requireContext().config.secretPin == pw) {
                        viewModel.setState(1)
                        clearPin()
                    }
                }
            }
            1 -> {
                val passwordLv2 = viewModel.state2Password.value
                passwordLv2?.let { pw ->
                    if (pw.length < 4) {
                        showSnackBar(
                            getString(R.string.password_must_be_4_characters), SnackBarType.FAILED
                        )
                        return
                    } else {
                        viewModel.setState(2)
                        clearPin()
                    }
                }
            }
            2 -> {
                val passwordLv3 = viewModel.state3Password.value
                passwordLv3?.let { pw ->
                    if (pw.length < 4) {
                        showSnackBar(
                            getString(R.string.password_must_be_4_characters), SnackBarType.FAILED
                        )
                        return
                    }
                    if (pw != viewModel.state2Password.value) {
                        showSnackBar(
                            getString(R.string.password_does_not_match), SnackBarType.FAILED
                        )
                        return
                    }
                    requireContext().config.secretPin = pw
                    showSnackBar(
                        getString(R.string.change_pin_success), SnackBarType.SUCCESS
                    )
                    popBackStack()
                }
            }
        }
    }


    private fun appendToPassword(key: String) {
        val state = viewModel.currentState.value
        state?.let {
            when (it) {
                0 -> {
                    val password = viewModel.state1Password.value
                    Log.d("TAG", "password: "+password)
                    password?.let { pw ->
                        if (pw.length < 4) {
                            viewModel.setState1Password("${pw}${key}")
                            checkView(viewModel.state1Password.value.toString())
                        }
                    }

                }
                1 -> {
                    val password = viewModel.state2Password.value
                    password?.let { pw ->
                        if (pw.length < 4) {
                            viewModel.setState2Password("${pw}${key}")
                            checkView(viewModel.state2Password.value.toString())
                        }
                    }

                }
                else -> {
                    val password = viewModel.state3Password.value
                    password?.let { pw ->
                        if (pw.length < 4) {
                            viewModel.setState3Password("${pw}${key}")
                            checkView(viewModel.state3Password.value.toString())
                        }
                    }

                }
            }
        }

    }

    private fun clearPin() {
        val groupPin = listOf(binding.char1, binding.char2, binding.char3, binding.char4)
        groupPin.forEach {
            it.setBackgroundResource(R.drawable.bg_pin_inactive)
        }
    }

    private fun checkView(password: String) {
        Log.d("TAG", "checkView: " +password)
        val groupPin = listOf(binding.char1, binding.char2, binding.char3, binding.char4)
        groupPin.forEachIndexed { index, item ->
            if (index < password.length) {
                item.setBackgroundResource(R.drawable.bg_pin_active)
            } else {
                item.setBackgroundResource(R.drawable.bg_pin_inactive)
            }
        }
    }


    private fun handleBackSpace() {
        val state = viewModel.currentState.value
        state?.let {
            when (it) {
                0 -> {
                    val password = viewModel.state1Password.value
                    password?.let { pw ->
                        if (pw.isNotEmpty()) {
                            val currentPassword = pw.subSequence(
                                0, pw.length - 1
                            ).toString()
                            viewModel.setState1Password(currentPassword)
                            checkView(viewModel.state1Password.value.toString())

                        }
                    }
                }
                1 -> {
                    val password = viewModel.state2Password.value
                    password?.let { pw ->
                        if (pw.isNotEmpty()) {
                            val currentPassword = pw.subSequence(
                                0, pw.length - 1
                            ).toString()
                            viewModel.setState2Password(currentPassword)
                            checkView(viewModel.state2Password.value.toString())
                        }

                    }


                }
                else -> {
                    val password = viewModel.state3Password.value
                    password?.let { pw ->
                        if (pw.isNotEmpty()) {
                            val currentPassword = pw.subSequence(
                                0, pw.length - 1
                            ).toString()
                            viewModel.setState3Password(currentPassword)
                            checkView(viewModel.state3Password.value.toString())
                        }

                    }


                }
            }
        }
    }


}