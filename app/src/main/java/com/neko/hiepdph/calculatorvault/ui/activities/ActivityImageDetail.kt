package com.neko.hiepdph.calculatorvault.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.adapter.ImagePagerAdapter
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import com.neko.hiepdph.calculatorvault.databinding.ActivityImageDetailBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogDetail
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityImageDetail : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding
    private var viewPagerAdapter: ImagePagerAdapter? = null
    private var currentItem: ListItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        initView()
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_image_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                openImageInformationDialog()
                true
            }
            else -> false
        }
    }

    private fun openImageInformationDialog() {
        val dialogDetail = DialogDetail.dialogDetailConfig {
            name = currentItem?.mName
            size = currentItem?.mSize
            path = currentItem?.mPath
        }
        dialogDetail.show(supportFragmentManager, dialogDetail.tag)
    }

    private fun getData() {
        ShareData.getInstance().listItemImage.observe(this) {
            viewPagerAdapter?.setData(it)
            if (it.isNotEmpty()) {
                currentItem = it[0]
            }
            if (it.size > 1) {
                supportActionBar?.title = "1/${it.size}"
            } else {
                supportActionBar?.title = String.EMPTY
            }
            binding.imageViewPager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    currentItem = it[position]
                    supportActionBar?.title = "${position + 1}/${it.size}"
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }
    }


    private fun initView() {
//        binding.actionBar.invisible()
//        binding.containerController.invisible()

        initViewPager()
        initButton()
    }

    private fun initViewPager() {
        viewPagerAdapter = ImagePagerAdapter(this)
        binding.imageViewPager.adapter = viewPagerAdapter

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

        binding.tvRotate.clickWithDebounce {
            viewPagerAdapter?.rotate(binding.imageViewPager.currentItem)
        }


    }

    private fun showController() {
        binding.containerController.show()
        binding.actionBar.show()
    }
}