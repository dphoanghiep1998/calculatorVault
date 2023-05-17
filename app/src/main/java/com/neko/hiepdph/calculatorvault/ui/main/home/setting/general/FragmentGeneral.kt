package com.neko.hiepdph.calculatorvault.ui.main.home.setting.general

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.config.SlideShowTransition
import com.neko.hiepdph.calculatorvault.databinding.FragmentGeneralBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogEncryptionMode
import com.neko.hiepdph.calculatorvault.dialog.DialogSlideShowInterval
import com.neko.hiepdph.calculatorvault.dialog.DialogSlideShowTransition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentGeneral : Fragment() {
    private lateinit var binding: FragmentGeneralBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGeneralBinding.inflate(inflater, container, false)
        AppSharePreference.INSTANCE.registerOnSharedPreferenceChangeListener(listener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        initView()
    }

    private fun initView() {
        setupView()
        initAction()
    }
    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            Constant.KEY_SLIDE_SHOW_INTERVAL -> binding.containerSlideshowInterval.tvStatus.text =
                String.format(
                    getString(R.string.sec), requireContext().config.slideShowInterval.toString()
                )

            Constant.KEY_SLIDE_SHOW_TRANSITION -> binding.containerSlideshowTransition.tvStatus.text =
                when (requireContext().config.slideShowTransition) {
                    SlideShowTransition.NATURAL -> getString(R.string.natural)
                    SlideShowTransition.NONE -> getString(R.string.none)
                    SlideShowTransition.BOTTOM -> getString(R.string.bottom)
                    SlideShowTransition.FADE -> getString(R.string.fade)
                    SlideShowTransition.LEFT -> getString(R.string.left)
                    SlideShowTransition.RIGHT -> getString(R.string.right)
                    SlideShowTransition.TOP -> getString(R.string.top)
                    SlideShowTransition.ROTATE -> getString(R.string.rotate)
                    SlideShowTransition.ZOOM -> getString(R.string.zoom)
                    else -> {
                        getString(R.string.random)
                    }
                }
            Constant.KEY_ENCRYPTION_MODE -> {
                binding.containerSelectAnEncryptionMode.tvStatus.text =
                    when (requireContext().config.encryptionMode) {
                        EncryptionMode.HIDDEN -> getString(R.string.hidden)
                        EncryptionMode.ENCRYPTION -> getString(R.string.encryption)
                        else -> getString(R.string.always_ask)
                    }
            }
        }
    }

    private fun initAction() {
        binding.containerRecycleBin.root.clickWithDebounce {
            binding.containerRecycleBin.switchChange.isChecked =
                !binding.containerRecycleBin.switchChange.isChecked
            requireContext().config.moveToRecyclerBin =
                binding.containerRecycleBin.switchChange.isChecked
        }
        binding.containerRecycleBin.switchChange.setOnClickListener {
            requireContext().config.moveToRecyclerBin =
                binding.containerRecycleBin.switchChange.isChecked
        }
        binding.containerSlideshowInterval.root.clickWithDebounce {

        }

        binding.containerSlideshowRandomPlay.root.clickWithDebounce {
            binding.containerSlideshowRandomPlay.switchChange.isChecked =
                !binding.containerSlideshowRandomPlay.switchChange.isChecked
            requireContext().config.slideRandomPlay =
                binding.containerSlideshowRandomPlay.switchChange.isChecked
        }
        binding.containerSlideshowRandomPlay.switchChange.setOnClickListener {
            requireContext().config.slideRandomPlay =
                binding.containerSlideshowRandomPlay.switchChange.isChecked
        }
        binding.containerShakeClose.root.clickWithDebounce {
            binding.containerShakeClose.switchChange.isChecked =
                !binding.containerShakeClose.switchChange.isChecked
            requireContext().config.shakeClose = binding.containerShakeClose.switchChange.isChecked
            if (binding.containerShakeClose.switchChange.isChecked) {
                binding.containerShakeClose.tvStatus.show()
                binding.containerShakeClose.sensitiveSeekbar.show()
            } else {
                binding.containerShakeClose.sensitiveSeekbar.hide()
                binding.containerShakeClose.tvStatus.hide()
            }
        }
        binding.containerShakeClose.switchChange.setOnClickListener {
            requireContext().config.shakeClose = binding.containerShakeClose.switchChange.isChecked
            if (binding.containerShakeClose.switchChange.isChecked) {
                binding.containerShakeClose.sensitiveSeekbar.show()
            } else {
                binding.containerShakeClose.sensitiveSeekbar.hide()
            }
        }
        binding.containerPlayVideoMode.root.setOnClickListener {
            binding.containerPlayVideoMode.switchChange.isChecked =
                !binding.containerPlayVideoMode.switchChange.isChecked

            requireContext().config.playVideoMode =
                binding.containerPlayVideoMode.switchChange.isChecked
        }
        binding.containerPlayVideoMode.switchChange.setOnClickListener {
            requireContext().config.playVideoMode =
                binding.containerPlayVideoMode.switchChange.isChecked
        }

        binding.containerSlideshowInterval.root.clickWithDebounce {
            val dialogSlideShowInterval = DialogSlideShowInterval()
            dialogSlideShowInterval.show(childFragmentManager, dialogSlideShowInterval.tag)
        }

        binding.containerSlideshowTransition.root.clickWithDebounce {
            val dialogSlideShowTransition = DialogSlideShowTransition()
            dialogSlideShowTransition.show(childFragmentManager, dialogSlideShowTransition.tag)
        }

        binding.containerSelectAnEncryptionMode.root.clickWithDebounce {
            val dialogEncryptionMode = DialogEncryptionMode()
            dialogEncryptionMode.show(childFragmentManager, dialogEncryptionMode.tag)
        }


        //init seekbar
        binding.containerShakeClose.sensitiveSeekbar.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.containerShakeClose.sensitiveSeekbar.progress = p1
                binding.containerShakeClose.tvStatus.text = p1.toString()
                requireContext().config.shakeGravity = p1.toFloat()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    private fun setupView() {
        binding.containerRecycleBin.apply {
            tvContent.text = getString(R.string.recycle_bin)
            switchChange.show()
            switchChange.isChecked = requireContext().config.moveToRecyclerBin
            tvContent.text = getString(R.string.recycle_bin)
            tvContent2.text = getString(R.string.recycle_bin_content_2)
            imvNext.hide()
            imvIcon.setImageResource(R.drawable.ic_delete)
        }

        binding.containerSlideshowInterval.apply {
            tvContent.text = getString(R.string.slide_show_interval)
            tvStatus.show()
            tvStatus.text = String.format(
                getString(R.string.sec), requireContext().config.slideShowInterval.toString()
            )
            imvIcon.setImageResource(R.drawable.ic_slideshow_interval)
        }
        binding.containerSlideshowTransition.apply {
            imvIcon.setImageResource(R.drawable.ic_slideshow_transition)
            tvContent.text = getString(R.string.slide_show_transition)
            tvStatus.show()
            tvStatus.text = when (requireContext().config.slideShowTransition) {
                SlideShowTransition.NATURAL -> getString(R.string.natural)
                SlideShowTransition.NONE -> getString(R.string.none)
                SlideShowTransition.BOTTOM -> getString(R.string.bottom)
                SlideShowTransition.FADE -> getString(R.string.fade)
                SlideShowTransition.LEFT -> getString(R.string.left)
                SlideShowTransition.RIGHT -> getString(R.string.right)
                SlideShowTransition.TOP -> getString(R.string.top)
                SlideShowTransition.ROTATE -> getString(R.string.rotate)
                SlideShowTransition.ZOOM -> getString(R.string.zoom)
                else -> {
                    getString(R.string.random)
                }
            }
        }

        binding.containerSlideshowRandomPlay.apply {
            tvContent.text = getString(R.string.slide_show_random_play)
            switchChange.show()
            switchChange.isChecked = requireContext().config.slideRandomPlay
            imvIcon.setImageResource(R.drawable.ic_slideshow_random)
            imvNext.hide()
        }

        binding.containerShakeClose.apply {
            tvContent.text = getString(R.string.shake_close)
            switchChange.show()
            switchChange.isChecked = requireContext().config.shakeClose
            tvContent2.show()
            tvContent2.text = getString(R.string.shake_close_content_2)
            if (requireContext().config.shakeClose) {
                tvStatus.show()
                sensitiveSeekbar.show()
            } else {
                tvStatus.hide()
                sensitiveSeekbar.hide()
            }
            tvStatus.text = requireContext().config.shakeGravity.toString()
            imvIcon.setImageResource(R.drawable.ic_shake_close)
            sensitiveSeekbar.max = 20
            sensitiveSeekbar.progress = requireContext().config.shakeGravity.toInt()
        }

        binding.containerPlayVideoMode.apply {
            tvContent.text = getString(R.string.play_video_mode)
            switchChange.show()
            switchChange.isChecked = requireContext().config.playVideoMode
            tvContent2.show()
            tvContent2.text = getString(R.string.play_video_mode_content_2)
            imvIcon.setImageResource(R.drawable.ic_play_video_mode)
            imvNext.hide()
        }

        binding.containerSelectAnEncryptionMode.apply {
            tvContent.text = getString(R.string.select_an_encryption_mode)
            tvContent2.show()
            tvContent2.text = getString(R.string.encryption_mode_content_2)
            tvStatus.show()
            tvStatus.text = when (requireContext().config.encryptionMode) {
                EncryptionMode.HIDDEN -> getString(R.string.hidden)
                EncryptionMode.ENCRYPTION -> getString(R.string.encryption)
                else -> getString(R.string.always_ask)
            }
            imvIcon.setImageResource(R.drawable.ic_encryption_mode)
        }


    }

    override fun onDestroy() {
        AppSharePreference.INSTANCE.unregisterListener(listener)
        super.onDestroy()
    }

}