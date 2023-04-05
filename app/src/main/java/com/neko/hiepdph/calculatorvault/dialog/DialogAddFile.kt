package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.databinding.DialogAddFileBinding


class DialogAddFile(
    private val onClickPicture: (() -> Unit)? = null,
    private val onClickVideo: (() -> Unit)? = null,
    private val onClickAudio: (() -> Unit)? = null,
    private val onClickFile: (() -> Unit)? = null,
) : DialogFragment() {

    private lateinit var binding: DialogAddFileBinding

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
        binding = DialogAddFileBinding.inflate(inflater, container, false)
        initFloatButton()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {


        binding.root.clickWithDebounce(1000) {
            closeMenu()
        }
        openMenu()
        initButton()

    }

    private fun initButton() {
        binding.picture.root.clickWithDebounce {
            binding.root.setOnClickListener {}
            closeMenu(action = {
                onClickPicture?.invoke()
            })

        }
        binding.video.root.clickWithDebounce {
            binding.root.setOnClickListener {}
            closeMenu(action = {
                onClickVideo?.invoke()
            })
        }
        binding.audio.root.clickWithDebounce {
            binding.root.setOnClickListener {}
            closeMenu(action = {
                onClickAudio?.invoke()
            })
        }
        binding.file.root.clickWithDebounce {
            binding.root.setOnClickListener {}
            closeMenu(action = {
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
//
//        binding.audio.root.apply {
//            isEnabled = true
//            animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300)
//                .start()
//        }
//        binding.video.root.apply {
//            isEnabled = true
//            animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(400)
//                .start()
//        }
//        binding.picture.root.apply {
//            isEnabled = true
//            animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(500)
//                .start()
//        }
    }

    private fun closeMenu(action: (() -> Unit)? = null) {
        val interpolator = OvershootInterpolator()
        binding.containerItem.apply {
            isEnabled = false
            animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(500)
                .withEndAction{
                    dismiss()
                    action?.invoke()
                }.start()
        }
//        binding.video.root.apply {
//            isEnabled = false
//            animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300)
//                .withEndAction {
//                    dismiss()
//                    action?.invoke()
//                }.start()
//        }
//        binding.audio.root.apply {
//            isEnabled = false
//            animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(400)
//                .start()
//        }
//        binding.file.root.apply {
//            isEnabled = false
//            animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(500)
//                .start()
//        }
    }

    private fun initFloatButton() {
        val listOfFloatMember = listOf(
            Pair(
                getString(R.string.pictures),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_file_picture)
            ),
            Pair(
                getString(R.string.videos),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_file_video)
            ),
            Pair(
                getString(R.string.audios),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_file_audio)
            ),
            Pair(
                getString(R.string.files),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_file_file)
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
            closeMenu()
        }

    }


}