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
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.utils.DateTimeUtils
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.DialogDetailBinding


class DialogDetail(
   private val fileVaultItem: FileVaultItem
) : DialogFragment() {
    private lateinit var binding: DialogDetailBinding

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
        binding = DialogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.tvTimeValue.text = DateTimeUtils.getDateConverted(fileVaultItem.modified)

        binding.tvNameValue.text = fileVaultItem.name

        binding.tvSizeValue.text = fileVaultItem.size.formatSize()

        if (fileVaultItem.ratioPicture == null) {
            binding.tvResolution.hide()
            binding.tvResolutionValue.hide()
        } else {
            binding.tvResolutionValue.text = fileVaultItem.ratioPicture
        }

        binding.tvTimeLockValue.text = DateTimeUtils.getDateConverted(fileVaultItem.timeLock)
        binding.tvPathValue.text = fileVaultItem.encryptedPath
        binding.tvOriginPathValue.text = fileVaultItem.originalPath
        if (fileVaultItem.encryptionType == EncryptionMode.HIDDEN) {
            binding.tvEncryptionModeValue.text = getString(R.string.hidden)
        } else {
            binding.tvEncryptionModeValue.text = getString(R.string.encryption_mode)
        }
        initButton()
    }

    private fun initButton() {

        binding.btnConfirm.clickWithDebounce {
            dismiss()
        }
        binding.root.clickWithDebounce {
            dismiss()
        }

        binding.containerMain.setOnClickListener { }
    }

    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
        }

    }
}