package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.databinding.DialogAskEncryptionModeBinding


class DialogAskEncryptionMode(private val onPressPositive: (selectedEncryption: Int) -> Unit) :
    DialogFragment() {
    private lateinit var binding: DialogAskEncryptionModeBinding
    private var selectedEncryption = 1

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
        binding = DialogAskEncryptionModeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
        binding.checkboxHidden.isChecked = true
        selectedEncryption = 1
    }

    private fun initButton() {
        binding.btnConfirm.clickWithDebounce {
            onPressPositive.invoke(selectedEncryption)
            dismiss()
        }
        binding.root.clickWithDebounce {
            dismiss()
        }
        binding.containerMain.setOnClickListener { }

        binding.checkboxEncryption.setOnClickListener {
            selectedEncryption = EncryptionMode.ENCRYPTION
        }
        binding.checkboxHidden.setOnClickListener {
            selectedEncryption = EncryptionMode.HIDDEN
        }


    }

    private fun openTips() {

    }

    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
        }

    }
}