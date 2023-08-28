package com.neko.hiepdph.calculatorvault.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.config.TemporaryTimeDeletion

class DialogTemporaryFileDeletionTime(val context: Context, val selectOption: Int) {
    private var currentSelect = 0
    fun onCreateDialog(activity: Activity): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_temporary_file_deletion)

        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        window.attributes = wlp
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        initView(dialog)
        return dialog
    }

    private fun initView(dialog: Dialog) {
        currentSelect = selectOption
        when (selectOption) {
            TemporaryTimeDeletion.LOCKED -> {
                dialog.findViewById<RadioButton>(R.id.checkbox_locked).isChecked = true
            }

            TemporaryTimeDeletion.EXIT_APP -> {
                dialog.findViewById<RadioButton>(R.id.checkbox_exit).isChecked = true
            }
        }
        dialog.findViewById<RadioButton>(R.id.checkbox_locked).clickWithDebounce {
            currentSelect = TemporaryTimeDeletion.LOCKED
        }
        dialog.findViewById<RadioButton>(R.id.checkbox_exit).clickWithDebounce {
            currentSelect = TemporaryTimeDeletion.EXIT_APP

        }
        dialog.findViewById<TextView>(R.id.btn_confirm_deletion).clickWithDebounce {
            context.config.temporaryFileDeletionTime = currentSelect
            dialog.dismiss()
        }
        dialog.findViewById<ConstraintLayout>(R.id.root).clickWithDebounce {
            dialog.dismiss()
        }
        dialog.findViewById<ConstraintLayout>(R.id.container_main).clickWithDebounce {
        }
    }
}