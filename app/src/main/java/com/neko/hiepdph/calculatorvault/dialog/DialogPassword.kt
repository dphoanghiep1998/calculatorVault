package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.databinding.DialogPasswordBinding


interface SetupPassWordCallBack {
    fun onPositiveClicked()
}

class DialogPassword(
    private val callBack: SetupPassWordCallBack
) : DialogFragment() {
    private lateinit var binding: DialogPasswordBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = DialogCallBack(requireContext(), callback)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(requireContext().getColor(R.color.blur)))

        dialog.window!!.setLayout(
            (requireContext().resources.displayMetrics.widthPixels),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
    }

    private fun initButton() {
        binding.question.dismissDropDown()

        binding.question.setText(requireContext().config.securityQuestion)


        binding.btnConfirm.clickWithDebounce {
            if (binding.tvContent.text.toString() == requireContext().config.securityAnswer) {
                callBack.onPositiveClicked()
                dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.security_answer_invalid),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.btnCancel.clickWithDebounce {
            dismiss()
        }
        binding.root.clickWithDebounce {
            dismiss()
        }

        binding.containerMain.setOnClickListener {  }
    }

    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
        }

    }
}