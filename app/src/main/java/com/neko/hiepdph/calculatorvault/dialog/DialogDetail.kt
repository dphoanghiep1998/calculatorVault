package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.utils.DateTimeUtils
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.DialogDetailBinding


class DialogDetail(
    private val context: Context, private val fileVaultItem: FileVaultItem
) {
    private lateinit var binding: DialogDetailBinding

    fun onCreateDialog(): Dialog {
        val root = ConstraintLayout(context)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = Dialog(context)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.blur)))
        binding = DialogDetailBinding.inflate(LayoutInflater.from(context))
        dialog.window!!.setLayout(
            (context.resources.displayMetrics.widthPixels), ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.setContentView(binding.root)
        initView(dialog)
        return dialog
    }


    private fun initView(dialog: Dialog) {
        binding.tvTimeValue.text = DateTimeUtils.getDateConverted(fileVaultItem.modified)

        binding.tvNameValue.text = fileVaultItem.name

        binding.tvSizeValue.text = fileVaultItem.size.formatSize()

        if (fileVaultItem.ratioPicture == null) {
            binding.tvResolution.hide()
            binding.tvResolutionValue.hide()
        } else {
            binding.tvResolutionValue.text = fileVaultItem.ratioPicture
        }

        binding.tvTimeLockValue.text = DateTimeUtils.getDateTimeConverted(fileVaultItem.timeLock)
        binding.tvPathValue.text = fileVaultItem.encryptedPath
        binding.tvOriginPathValue.text = fileVaultItem.originalPath
        if (fileVaultItem.encryptionType == EncryptionMode.HIDDEN) {
            binding.tvEncryptionModeValue.text = context.getString(R.string.hidden)
        } else {
            binding.tvEncryptionModeValue.text = context.getString(R.string.encryption_mode)
        }
        initButton(dialog)
    }

    private fun initButton(dialog: Dialog) {

        binding.btnConfirm.clickWithDebounce {
            dialog.dismiss()
        }
        binding.root.clickWithDebounce {
            dialog.dismiss()
        }

        binding.containerMain.setOnClickListener { }
    }

}