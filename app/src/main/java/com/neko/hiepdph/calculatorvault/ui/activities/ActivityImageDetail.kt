package com.neko.hiepdph.calculatorvault.ui.activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.jsibbold.zoomage.ZoomageView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.databinding.ActivityImageDetailBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogCallBack
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import com.neko.hiepdph.calculatorvault.viewmodel.PersistentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityImageDetail : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        getData()
        initView()
    }
    private fun getData(){
        ShareData.getInstance().listItemImage.observe(this){
            Log.d("TAG", "getData: "+ it.size)
            it.forEach {item ->
                val imageView = ZoomageView(this)
                val root = FrameLayout(this)
                root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                imageView.window.setLayout(
                    (requireContext().resources.displayMetrics.widthPixels),
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                imageView
                Glide.with(this).load(item.mPath).error(R.drawable.ic_global).into(imageView)
                binding.flipView.addView(imageView)
            }
        }
    }


    private fun initView() {
        initButton()
    }

    private fun initButton() {
        binding.tvUnlock.clickWithDebounce {

        }

        binding.tvDelete.clickWithDebounce {

        }

        binding.tvShare.clickWithDebounce {

        }

        binding.tvSlideshow.clickWithDebounce {

        }
    }
}