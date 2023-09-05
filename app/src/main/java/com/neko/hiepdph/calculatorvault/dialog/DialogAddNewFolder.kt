package com.neko.hiepdph.calculatorvault.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.databinding.DialogAddFileBinding
import com.neko.hiepdph.calculatorvault.databinding.DialogAddFolderBinding


interface AddNewFolderDialogCallBack {
    fun onPositiveClicked(name: String)
}

class DialogAddNewFolder(
    private val context: Context, private val callBack: AddNewFolderDialogCallBack
) {
    private lateinit var binding: DialogAddFolderBinding
    fun onCreateDialog(activity: Activity): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        binding = DialogAddFolderBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        window.attributes = wlp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.transparent)))
        dialog.window!!.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        initView(dialog)
        return dialog
    }


    private fun initView(dialog: Dialog) {
        initButton(dialog)
    }

    private fun initButton(dialog: Dialog) {
        binding.btnConfirm.clickWithDebounce {
            if (binding.edtName.text.isNotBlank()) {
                callBack.onPositiveClicked(binding.edtName.text.toString())
                dialog.dismiss()
            } else {
                dialog.ownerActivity?.showSnackBar(
                    context.getString(R.string.invalid_folder_name), SnackBarType.FAILED
                )
            }
        }
        binding.btnCancel.clickWithDebounce {
            dialog.dismiss()
        }
        binding.root.clickWithDebounce {
            dialog.dismiss()
        }
        binding.containerMain.clickWithDebounce {

        }
    }

}