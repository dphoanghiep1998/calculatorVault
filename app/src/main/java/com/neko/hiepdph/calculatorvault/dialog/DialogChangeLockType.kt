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
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.config.LockType
import com.neko.hiepdph.calculatorvault.databinding.DialogLockTypeBinding

class DialogChangeLockType : DialogFragment() {
    private lateinit var binding: DialogLockTypeBinding
    private var currentLockType = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(requireContext().getColor(R.color.transparent)))

        dialog.window!!.setLayout(
            (requireContext().resources.displayMetrics.widthPixels),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogLockTypeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initData()
        initButton()
    }

    private fun initData() {
        currentLockType = requireContext().config.lockType
        when (requireContext().config.lockType) {
            LockType.PATTERN -> binding.checkboxPattern.isChecked = true
            LockType.PIN -> binding.checkboxPin.isChecked = true
            LockType.NONE -> binding.checkboxNone.isChecked = true
        }
    }

    private fun initButton() {
        binding.root.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.containerMain.setOnClickListener {

        }
        binding.checkboxPattern.setOnClickListener {
            currentLockType = LockType.PATTERN
        }
        binding.checkboxPin.setOnClickListener {
            currentLockType = LockType.PIN
        }
        binding.checkboxNone.setOnClickListener {
            currentLockType = LockType.NONE
        }
        binding.btnConfirm.clickWithDebounce {
            requireContext().config.lockType = currentLockType
            findNavController().popBackStack()
        }
    }
}