package com.neko.hiepdph.calculatorvault.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import com.neko.hiepdph.calculatorvault.databinding.ActivityVideoPlayerBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogDetail
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityVideoPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityVideoPlayerBinding
    private var listItem = mutableListOf<ListItem>()
    private var currentItem: ListItem? = null
    private var player: Player? = null

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
                    item.mPath
                })
            }
        }
    }

    private fun initView() {
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
    }

    private fun setupPlayer(listPath: List<String?>) {
        try {
            val listMediaItem = mutableListOf<MediaItem>()
            listPath.forEach {
                listMediaItem.add(MediaItem.fromUri(it ?: ""))
            }

            player?.addMediaItems(listMediaItem)
            player?.prepare()
            player?.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}