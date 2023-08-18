package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.enums.Action
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.shareFile
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.config.Status
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.ActivityVideoPlayerBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.dialog.DialogDetail
import com.neko.hiepdph.calculatorvault.dialog.DialogProgress
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ActivityVideoPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding
    private val viewModel by viewModels<AppViewModel>()
    private var listItem = mutableListOf<FileVaultItem>()
    private var currentItem: FileVaultItem? = null
    private var mPlayer: Player? = null
    private var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        supportActionBar?.title = String.EMPTY
        getData()
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_image_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                openInformationDialog()
                true
            }

            else -> false
        }
    }

    private fun openInformationDialog() {
        val dialogDetail = currentItem?.let { DialogDetail(this, it).onCreateDialog() }
        dialogDetail?.show()
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun getData() {
        ShareData.getInstance().listItemVideo.observe(this) {
            listItem.clear()
            listItem.addAll(it)

            if (it.isNotEmpty()) {
                currentItem = listItem[0]
                supportActionBar?.title = currentItem?.name
                setupPlayer(it.map { item ->
                    item.decodePath
                })
            }
        }
    }

    private fun initView() {
        mPlayer = ExoPlayer.Builder(this).setSeekForwardIncrementMs(15000).build()

        binding.playerView.apply {
            player = mPlayer
            keepScreenOn = true

        }


        initButton()
    }

    private fun initButton() {
        findViewById<ImageView>(R.id.imv_fullscreen).clickWithDebounce {
            if (flag) {
                supportActionBar?.show()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                flag = false
                findViewById<ImageView>(R.id.imv_fullscreen).setImageResource(R.drawable.ic_full_screen)
                showSystemUI()

            } else {
                supportActionBar?.hide()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                flag = true
                findViewById<ImageView>(R.id.imv_fullscreen).setImageResource(R.drawable.ic_small_screen)
                hideSystemUI()

            }
        }

        findViewById<TextView>(R.id.tv_unlock).clickWithDebounce {
            showDialogUnlock()
        }
        findViewById<TextView>(R.id.tv_share).clickWithDebounce {
            val listPath = mutableListOf<String>()
            listPath.add(currentItem!!.encryptedPath)
            shareFile(listPath)
        }
        findViewById<TextView>(R.id.tv_delete).clickWithDebounce {
            val confirmDialog = DialogConfirm(onPositiveClicked = {
                val dialogProgress = DialogProgress(listItemSelected = mutableListOf(currentItem!!),
                    listOfSourceFile = mutableListOf(File(currentItem!!.encryptedPath)),
                    listOfTargetParentFolder = mutableListOf(config.recyclerBinFolder),
                    action = Action.DELETE,
                    onResult = { status, statusText ->
                        if (status == Status.SUCCESS) {
                            listItem.remove(currentItem)
                            if (listItem.isEmpty()) {
                                ShareData.getInstance().setListItemImage(mutableListOf())
                                finish()
                            } else {
                                ShareData.getInstance().setListItemImage(listItem)
                            }
                            showSnackBar(statusText, SnackBarType.SUCCESS)
                        }
                        if (status == Status.FAILED) {
                            showSnackBar(statusText, SnackBarType.FAILED)
                        }

                        if(status == Status.WARNING){
                            showSnackBar(statusText, SnackBarType.WARNING)
                        }
                    })
                dialogProgress.show(supportFragmentManager, dialogProgress.tag)
            }, DialogConfirmType.DELETE, currentItem?.name)

            confirmDialog.show(supportFragmentManager, confirmDialog.tag)

        }
    }

    private fun showDialogUnlock() {
        val name = getString(R.string.pictures)
        val confirmDialog = DialogConfirm(onPositiveClicked = {
            unLockVideo()
        }, DialogConfirmType.UNLOCK, name)

        confirmDialog.show(supportFragmentManager, confirmDialog.tag)
    }

    private fun unLockVideo() {
        val dialogProgress = DialogProgress(
            listItemSelected = mutableListOf(currentItem!!),
            listOfSourceFile = mutableListOf(File(currentItem?.encryptedPath.toString())),
            listOfTargetParentFolder = mutableListOf(File(currentItem?.originalPath.toString()).parentFile),
            onResult = { listOfFileDeletedSuccess, listOfFileDeletedFailed ->

            },
            action = Action.UNLOCK
        )
        lifecycleScope.launch {
            CopyFiles.copy(this@ActivityVideoPlayer,
                mutableListOf(File(currentItem?.encryptedPath.toString())),
                mutableListOf(File(currentItem?.originalPath.toString())),
                0L,
                progress = { _: Float, _: File? -> },
                onResult = { listOfFileDeletedSuccess, listOfFileDeletedFailed -> })
//                onSuccess = {
//                    listItem.remove(currentItem)
//                    if (listItem.isEmpty()) {
//                        ShareData.getInstance().setListItemImage(mutableListOf())
//                        finish()
//                    } else {
//                        ShareData.getInstance().setListItemImage(listItem)
//                    }
//                },
//                onError = {})
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window, window.decorView
        ).show(WindowInsetsCompat.Type.systemBars())
    }

    private fun setupPlayer(listPath: List<String?>) {
        try {
            val listMediaItem = mutableListOf<MediaItem>()
            listPath.forEach {
                listMediaItem.add(MediaItem.fromUri(it ?: ""))
            }

            mPlayer?.addMediaItems(listMediaItem)
            mPlayer?.prepare()
            mPlayer?.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        mPlayer?.apply {
            playWhenReady = false
            playbackState
        }
    }

    override fun onRestart() {
        super.onRestart()
        mPlayer?.apply {
            playWhenReady = true
            playbackState
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
        showSystemUI()
    }
}