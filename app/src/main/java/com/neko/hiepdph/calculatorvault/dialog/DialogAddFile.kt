package com.neko.hiepdph.calculatorvault.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.databinding.DialogAddFileBinding


class DialogAddFile(
    private val context: Context,
    private val onClickPicture: (() -> Unit)? = null,
    private val onClickVideo: (() -> Unit)? = null,
    private val onClickAudio: (() -> Unit)? = null,
    private val onClickFile: (() -> Unit)? = null,
) {

    private lateinit var binding: DialogAddFileBinding


    fun onCreateDialog(activity: Activity): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val window = dialog.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.CENTER
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        binding = DialogAddFileBinding.inflate(LayoutInflater.from(context))
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
        binding.root.clickWithDebounce(1000) {
            closeMenu(dialog)
        }
        openMenu()
        initButton(dialog)
        initFloatButton(dialog)

    }

    private fun initButton(dialog: Dialog) {
        binding.picture.root.clickWithDebounce {
            binding.root.setOnClickListener {}
            closeMenu(dialog, action = {
                onClickPicture?.invoke()
            })

        }
        binding.video.root.clickWithDebounce {
            binding.root.setOnClickListener {}
            closeMenu(dialog, action = {
                onClickVideo?.invoke()
            })
        }
        binding.audio.root.clickWithDebounce {
            binding.root.setOnClickListener {}
            closeMenu(dialog, action = {
                onClickAudio?.invoke()
            })
        }
        binding.file.root.clickWithDebounce {
            binding.root.setOnClickListener {}
            closeMenu(dialog, action = {
                onClickFile?.invoke()
            })
        }

    }

    private fun openMenu() {
        val interpolator = OvershootInterpolator()
        binding.floatingActionButton.setImageResource(R.drawable.ic_floating_close)

        binding.containerItem.apply {
            isEnabled = true
            animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500)
                .start()
        }
    }

    private fun closeMenu(dialog: Dialog, action: (() -> Unit)? = null) {
        val interpolator = OvershootInterpolator()
        binding.containerItem.apply {
            isEnabled = false
            animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(500)
                .withEndAction {
                    dialog.dismiss()
                    action?.invoke()
                }.start()
        }
    }

    private fun initFloatButton(dialog: Dialog) {
        val listOfFloatMember = listOf(
            Pair(
                context.getString(R.string.pictures),
                ContextCompat.getDrawable(context, R.drawable.ic_add_file_picture)
            ),
            Pair(
                context.getString(R.string.videos),
                ContextCompat.getDrawable(context, R.drawable.ic_add_file_video)
            ),
            Pair(
                context.getString(R.string.audios),
                ContextCompat.getDrawable(context, R.drawable.ic_add_file_audio)
            ),
            Pair(
                context.getString(R.string.files),
                ContextCompat.getDrawable(context, R.drawable.ic_add_file_file)
            ),
        )
        val listButton = listOf(
            binding.picture,
            binding.video,
            binding.audio,
            binding.file,
        )

        listButton.forEachIndexed { index, item ->
            item.tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                null, listOfFloatMember[index].second, null, null
            )
            item.tvTitle.text = listOfFloatMember[index].first
        }
        binding.floatingActionButton.clickWithDebounce(200) {
            closeMenu(dialog)
        }

    }


}