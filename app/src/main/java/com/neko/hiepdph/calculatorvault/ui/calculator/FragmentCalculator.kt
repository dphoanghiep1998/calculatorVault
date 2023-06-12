package com.neko.hiepdph.calculatorvault.ui.calculator

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.CustomApplication
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.customview.CalculatorFunction
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.DIVIDE_SYMBOL
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.common.utils.PI_SYMBOL
import com.neko.hiepdph.calculatorvault.common.utils.SQRT_SYMBOL
import com.neko.hiepdph.calculatorvault.config.ButtonToUnlock
import com.neko.hiepdph.calculatorvault.databinding.FragmentCalculatorBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogChangeTheme
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import dagger.hilt.android.AndroidEntryPoint
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat

@AndroidEntryPoint
class FragmentCalculator : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        AppSharePreference.INSTANCE.registerOnSharedPreferenceChangeListener(listener)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        initInputText()
        initCalculator()
        initResultView()
        initButton()

        if (requireContext().config.isSetupPasswordDone) {
            binding.containerInstruction.hide()
        } else {
            binding.containerInstruction.show()
        }
    }


    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
        if (p1 == Constant.KEY_THEME_COLOR) {
            setThemeButton(AppSharePreference.INSTANCE.getThemeColor(getColor(R.color.theme_default)))
        }

    }

    private fun setThemeButton(color: Int) {
        binding.layoutCalculator.setColorButton(color)
    }

    private fun initInputText() {
        binding.tvInput.movementMethod = ScrollingMovementMethod()
    }


    private fun initButton() {
        binding.imvChangeTheme.clickWithDebounce {
            val dialogChangeTheme = DialogChangeTheme()
            dialogChangeTheme.show(childFragmentManager, dialogChangeTheme.tag)
        }
        binding.tvCalculator.setOnLongClickListener {
            startActivity(Intent(requireContext(), ActivityVault::class.java))
            requireActivity().finish()
            return@setOnLongClickListener true
        }

        binding.tvCalculatorHighlight.setOnLongClickListener {
            startActivity(Intent(requireContext(), ActivityVault::class.java))
            requireActivity().finish()
            return@setOnLongClickListener true
        }
    }


    private fun initResultView() {
        binding.tvResult.text = String.EMPTY
    }

    private fun appendTextToInput(input: String) {
        binding.tvInput.append(input)
    }


    private fun checkSecretKey() {

        if (requireContext().config.isSetupPasswordDone && binding.tvInput.text.toString() == requireContext().config.secretPin) {
            (requireActivity().application as CustomApplication).authority = true
            startActivity(Intent(requireContext(), ActivityVault::class.java))
        }
    }

    private fun checkResult() {
        if (requireContext().config.buttonToUnlock == ButtonToUnlock.NONE) {
            checkSecretKey()
        }
    }


    private fun initCalculator() {
        binding.layoutCalculator.setCalculatorMainCallback(object : CalculatorFunction {
            override fun onPressButton0() {
                changeLayoutToInput()
                appendTextToInput("0")

            }

            override fun onPressButton1() {
                changeLayoutToInput()
                appendTextToInput("1")
            }

            override fun onPressButton2() {
                changeLayoutToInput()
                appendTextToInput("2")
            }

            override fun onPressButton3() {
                changeLayoutToInput()
                appendTextToInput("3")
            }

            override fun onPressButton4() {
                changeLayoutToInput()
                appendTextToInput("4")
            }

            override fun onPressButton5() {
                changeLayoutToInput()
                appendTextToInput("5")
            }

            override fun onPressButton6() {
                changeLayoutToInput()
                appendTextToInput("6")
            }

            override fun onPressButton7() {
                changeLayoutToInput()
                appendTextToInput("7")
            }

            override fun onPressButton8() {
                changeLayoutToInput()
                appendTextToInput("8")
            }

            override fun onPressButton9() {
                changeLayoutToInput()
                appendTextToInput("9")
            }

            override fun onPressButtonComma() {
                changeLayoutToInput()
                appendTextToInput(",")
            }

            override fun onPressButtonPlus() {
                changeLayoutToInput()
                appendTextToInput("+")
            }

            override fun onPressButtonSubtract() {
                changeLayoutToInput()
                appendTextToInput("-")
            }

            override fun onPressButtonTimes() {
                changeLayoutToInput()
                appendTextToInput("x")
            }

            override fun onPressButtonDivide() {
                changeLayoutToInput()
                appendTextToInput(DIVIDE_SYMBOL)
            }

            override fun onPressButtonEqual() {
                showResult()
                if (requireContext().config.buttonToUnlock == ButtonToUnlock.SHORT_PRESS) {
                    checkSecretKey()
                }
            }

            override fun onLongPressButtonEqual() {
                if (requireContext().config.buttonToUnlock == ButtonToUnlock.LONG_PRESS) {
                    checkSecretKey()
                }
            }

            override fun onPressButtonE() {
                changeLayoutToInput()
                appendTextToInput("e")
            }

            override fun onPressButtonSwitch() {
            }

            override fun onPressButtonReset() {
                changeLayoutToInput()
                binding.tvInput.text = ""
                binding.tvResult.text = ""
            }

            override fun onPressButtonBackspace() {
//                removeLastCharacter()
                handlePressBackSpace()
            }

            override fun onPressButtonPercent() {
                changeLayoutToInput()
                appendTextToInput("%")
            }

            override fun onPressButtonPi() {
                changeLayoutToInput()
                appendTextToInput(PI_SYMBOL)
            }

            override fun onPressButtonInverse() {
                changeLayoutToInput()
                appendTextToInput("1$DIVIDE_SYMBOL")
            }

            override fun onPressButtonExponent() {
                changeLayoutToInput()
                appendTextToInput("!")
            }

            override fun onPressButtonSqrt() {
                changeLayoutToInput()
                appendTextToInput("$SQRT_SYMBOL(")
            }

            override fun onPressButtonPow() {
                changeLayoutToInput()
                appendTextToInput("^")
            }

            override fun onPressButtonLg() {
                changeLayoutToInput()
                appendTextToInput("lg(")
            }

            override fun onPressButtonLn() {
                changeLayoutToInput()
                appendTextToInput("ln(")
            }

            override fun onPressButtonOpenBracket() {
                changeLayoutToInput()
                appendTextToInput("(")
            }

            override fun onPressButtonCloseBracket() {
                changeLayoutToInput()
                appendTextToInput(")")
            }

            override fun onPressButton2nd() {
            }

            override fun onPressButtonDeg() {

            }

            override fun onPressButtonRad() {
            }

            override fun onPressButtonSin() {
                changeLayoutToInput()
                appendTextToInput("sin(")
            }

            override fun onPressButtonCos() {
                changeLayoutToInput()
                appendTextToInput("cos(")
            }

            override fun onPressButtonTan() {
                changeLayoutToInput()
                appendTextToInput("tan(")
            }

            override fun onPressButtonArcSin() {
                changeLayoutToInput()
                appendTextToInput("arcsin(")
            }

            override fun onPressButtonArcCos() {
                changeLayoutToInput()
                appendTextToInput("arccos(")
            }

            override fun onPressButtonArcTan() {
                changeLayoutToInput()
                appendTextToInput("arctan(")
            }

        })
    }

    private fun handlePressBackSpace() {
        changeLayoutToInput()
        if (binding.tvInput.text.isNotBlank()) {
            when {
                binding.tvInput.text.endsWith("sin(") -> {
                    binding.tvInput.text = binding.tvInput.text.toString().replace("sin(", "")
                }

                binding.tvInput.text.endsWith("cos(") -> {
                    binding.tvInput.text = binding.tvInput.text.toString().replace("cos(", "")
                }

                binding.tvInput.text.endsWith("arcsin(") -> {
                    binding.tvInput.text = binding.tvInput.text.toString().replace("arcsin(", "")
                }

                binding.tvInput.text.endsWith("arccos(") -> {
                    binding.tvInput.text = binding.tvInput.text.toString().replace("arccos(", "")
                }

                binding.tvInput.text.endsWith("arctan(") -> {
                    binding.tvInput.text = binding.tvInput.text.toString().replace("arctan(", "")
                }

                binding.tvInput.text.endsWith("tan(") -> {
                    binding.tvInput.text = binding.tvInput.text.toString().replace("tan(", "")
                }

                binding.tvInput.text.endsWith("lg(") -> {
                    binding.tvInput.text = binding.tvInput.text.toString().replace("lg(", "")
                }

                binding.tvInput.text.endsWith("ln(") -> {
                    binding.tvInput.text = binding.tvInput.text.toString().replace("ln(", "")
                }

                binding.tvInput.text.endsWith("$SQRT_SYMBOL(") -> {
                    binding.tvInput.text =
                        binding.tvInput.text.toString().replace("$SQRT_SYMBOL(", "")
                }

                else -> {
                    binding.tvInput.text = binding.tvInput.text.subSequence(
                        0, binding.tvInput.text.toString().length - 1
                    )
                }
            }

        }
    }

    private fun changeLayoutToShowResult() {
        binding.tvResult.textSize = 60f
        binding.tvResult.setTextColor(getColor(R.color.neutral_06))
        binding.tvInput.textSize = 40f
        binding.tvInput.setTextColor(getColor(R.color.neutral_04))
    }

    private fun changeLayoutToInput() {
        binding.tvResult.textSize = 40f
        binding.tvResult.setTextColor(getColor(R.color.neutral_04))
        binding.tvInput.textSize = 60f
        binding.tvInput.setTextColor(getColor(R.color.neutral_06))
    }

    private fun getInputExpression(): String {
        var expression = binding.tvInput.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("x"), "*")
        expression = expression.replace(Regex(","), ".")
        return expression
    }

    private fun showResult() {
        try {
            val expression = getInputExpression()
            val result = Expression(expression).calculate()
            if (result.isNaN()) {
                // Show Error Message
                binding.tvResult.text = getString(R.string.invalid_expression)
            } else {
                // Show Result
                changeLayoutToShowResult()
                binding.tvResult.text = DecimalFormat("0.######").format(result).toString()
            }
        } catch (e: Exception) {
            // Show Error Message
            binding.tvResult.text = getString(R.string.invalid_expression)
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
        AppSharePreference.INSTANCE.unregisterListener(listener)

    }

}