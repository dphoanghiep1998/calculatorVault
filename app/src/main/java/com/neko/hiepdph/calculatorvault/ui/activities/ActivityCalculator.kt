package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.customview.CalculatorFunction
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.utils.DIVIDE_SYMBOL
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.common.utils.PI_SYMBOL
import com.neko.hiepdph.calculatorvault.common.utils.SQRT_SYMBOL
import com.neko.hiepdph.calculatorvault.config.LockType
import com.neko.hiepdph.calculatorvault.databinding.ActivityCaculatorBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogChangeTheme
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat

class ActivityCalculator : AppCompatActivity() {
    private lateinit var binding: ActivityCaculatorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

    }



    private fun initView() {
        initInputText()
        initCalculator()
        initResultView()
        initButton()
        observePassword()
    }

    private fun initInputText() {
        binding.tvInput.movementMethod = ScrollingMovementMethod()
    }

    private fun observePassword() {
        binding.tvInput.doOnTextChanged { text, start, before, count ->
            if (text.toString() == "${Constant.DEFAULT_SECRET_PASSWORD}${Constant.DEFAULT_SECRET_PASSWORD_SUFFIX}") {
                val intent = Intent(this, ActivityVault::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initButton() {
        binding.imvChangeTheme.clickWithDebounce {
            val dialogChangeTheme = DialogChangeTheme()
            dialogChangeTheme.show(supportFragmentManager, dialogChangeTheme.tag)
        }
        binding.tvCalculator.setOnLongClickListener {
            startActivity(Intent(this@ActivityCalculator, ActivityVault::class.java))
            finish()
            return@setOnLongClickListener true
        }
    }


    private fun initResultView() {
        binding.tvResult.text = String.EMPTY
    }

    private fun appendTextToInput(input: String) {
        binding.tvInput.append(input)
    }


//    private fun removeLastCharacter() {
//        val lastText = binding.tvInput.text.toString()
//        if (lastText.isNotBlank()) {
//            val subText = lastText.substring(0, lastText.length - 1)
//            binding.tvInput.setText(subText)
//        }
//    }

//    private fun checkSecretKey() {
//        if (binding.tvInput.text == "1234%") {
//            navigateToPage(R.id.fragmentCalculator, R.id.action_fragmentCalculator_to_fragmentHome)
//        }
//    }


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
                binding.tvInput.text.endsWith("${SQRT_SYMBOL}(") -> {
                    binding.tvInput.text =
                        binding.tvInput.text.toString().replace("${SQRT_SYMBOL}(", "")
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
}