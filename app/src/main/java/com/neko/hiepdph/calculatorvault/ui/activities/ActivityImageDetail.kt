package com.neko.hiepdph.calculatorvault.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.adapter.ImagePagerAdapter
import com.neko.hiepdph.calculatorvault.common.adapter.TapViewListener
import com.neko.hiepdph.calculatorvault.common.customview.FixedSpeedScroller
import com.neko.hiepdph.calculatorvault.common.enums.Action
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.shareFile
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.common.transformer.BottomTransformer
import com.neko.hiepdph.calculatorvault.common.transformer.FadeTransformer
import com.neko.hiepdph.calculatorvault.common.transformer.LeftTransformer
import com.neko.hiepdph.calculatorvault.common.transformer.RightTransformer
import com.neko.hiepdph.calculatorvault.common.transformer.RotationTransformer
import com.neko.hiepdph.calculatorvault.common.transformer.TopTransformer
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.ActivityImageDetailBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.dialog.DialogDetail
import com.neko.hiepdph.calculatorvault.dialog.DialogProgress
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import `in`.dd4you.animatoo.VpAnimatoo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Field


@AndroidEntryPoint
class ActivityImageDetail : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding
    private var viewPagerAdapter: ImagePagerAdapter? = null
    private var listItem: MutableList<FileVaultItem> = mutableListOf()
    private var currentItem: FileVaultItem? = null
    private var currentPage = 0
    private var jobSlide: Job? = null
    private val viewModel by viewModels<AppViewModel>()
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
        val dialogDetail = currentItem?.let { DialogDetail(this, it).onCreateDialog() }
        dialogDetail?.show()
    }

    private fun getData() {
        ShareData.getInstance().listItemImage.observe(this) {
            listItem = it
            viewPagerAdapter = ImagePagerAdapter(this, it)
            viewPagerAdapter?.setListener(object : TapViewListener {
                override fun onTap() {
                    jobSlide?.cancel()
                    showController()
//                    viewPagerAdapter = ImagePagerAdapter(this@ActivityImageDetail, it)
//                    binding.imageViewPager.adapter = viewPagerAdapter
//                    binding.imageViewPager.setCurrentItem(currentPage, false)

                }
            })
            binding.imageViewPager.adapter = viewPagerAdapter
            if (it.isNotEmpty()) {
                currentItem = it[0]
            }
            if (it.size > 1) {
                supportActionBar?.title = "${binding.imageViewPager.currentItem + 1}/${it.size}"
            } else {
                supportActionBar?.title = String.EMPTY
            }

            binding.imageViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    currentItem = it[position]
                    currentPage = position
                    supportActionBar?.title = "${position + 1}/${it.size}"
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })
        }
    }


    private fun initView() {
        initButton()
    }


    private fun initButton() {

        binding.tvUnlock.clickWithDebounce {
            showDialogUnlock()
        }

        binding.tvDelete.clickWithDebounce {
            val confirmDialog = DialogConfirm(onPositiveClicked = {
                if (config.moveToRecyclerBin) {
                    val dialogProgress =
                        DialogProgress(listItemSelected = mutableListOf(currentItem!!),
                            listOfSourceFile = mutableListOf(File(currentItem!!.encryptedPath)),
                            listOfTargetParentFolder = mutableListOf(config.recyclerBinFolder),
                            action = Action.DELETE,
                            onSuccess = {
                                ShareData.getInstance().setListItemImage(mutableListOf())
                                finish()
                                showSnackBar(it, SnackBarType.SUCCESS)

                            },
                            onFailed = {
                                showSnackBar(it, SnackBarType.FAILED)
                            })
                    dialogProgress.show(supportFragmentManager, dialogProgress.tag)
                } else {

                }
            }, DialogConfirmType.DELETE, getString(R.string.pictures))


            confirmDialog.show(supportFragmentManager, confirmDialog.tag)
        }

        binding.tvShare.clickWithDebounce {
            val listPath = mutableListOf<String>()
            listPath.add(currentItem!!.decodePath)
            shareFile(listPath)
        }

        binding.tvSlideshow.clickWithDebounce {
            autoScroll()
            hideController()
        }

        binding.tvRotate.clickWithDebounce {
            viewPagerAdapter?.rotate(binding.imageViewPager.currentItem)
        }


    }

    private fun hideController() {
        binding.containerController.hide()
        binding.actionBar.hide()
    }

    private fun autoScroll() {
        val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
        mScroller.isAccessible = true
        val sInterpolator = AccelerateInterpolator()
        val scroller = FixedSpeedScroller(this, sInterpolator)

        mScroller.set(binding.imageViewPager, scroller)

        when (config.slideShowTransition) {
            0 -> {}
            1 -> binding.imageViewPager.setPageTransformer(false, null)
            2 -> {}
            3 -> binding.imageViewPager.setPageTransformer(false, FadeTransformer())
            4 -> binding.imageViewPager.setPageTransformer(false, VpAnimatoo.ZoomIn())
            5 -> binding.imageViewPager.setPageTransformer(false, RotationTransformer())
            6 -> binding.imageViewPager.setPageTransformer(false, LeftTransformer())
            7 -> binding.imageViewPager.setPageTransformer(false, RightTransformer())
            8 -> binding.imageViewPager.setPageTransformer(false, TopTransformer())
            9 -> binding.imageViewPager.setPageTransformer(false, BottomTransformer())
        }
        jobSlide = lifecycleScope.launch {
            while (isActive) {
                delay(config.slideShowInterval * 1000L)
                currentPage++
                if (currentPage == viewPagerAdapter!!.count) {
                    currentPage = 0
                }

                binding.imageViewPager.setCurrentItem(currentPage, true)

            }
        }
        jobSlide?.start()
    }

    private fun showController() {
        binding.containerController.show()
        binding.actionBar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        jobSlide?.cancel()
    }


    private fun showDialogUnlock() {
        val name = getString(R.string.pictures)
        val confirmDialog = DialogConfirm(onPositiveClicked = {
            unLockPicture()
        }, DialogConfirmType.UNLOCK, name)

        confirmDialog.show(supportFragmentManager, confirmDialog.tag)


    }


    private fun unLockPicture() {

        val dialogProgress = DialogProgress(listItem,
            mutableListOf(File(currentItem?.encryptedPath.toString())),
            mutableListOf(File(currentItem?.encryptedPath.toString())),
            Action.UNLOCK,
            onSuccess = {
                viewModel.deleteFileVault(listItem.map { it.id }.toMutableList())
                runOnUiThread {
                    showSnackBar(
                        String.format(it), SnackBarType.SUCCESS
                    )
                }
                listItem.remove(currentItem)
                if (listItem.isEmpty()) {
                    ShareData.getInstance().setListItemImage(mutableListOf())
                    finish()
                } else {
                    ShareData.getInstance().setListItemImage(listItem)
                }

            },
            onFailed = {
                runOnUiThread {
                    showSnackBar((it), SnackBarType.FAILED)
                }
            })
        dialogProgress.show(supportFragmentManager, dialogProgress.tag)
    }


}