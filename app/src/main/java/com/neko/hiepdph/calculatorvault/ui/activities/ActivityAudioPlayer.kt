package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.getReadableTime
import com.neko.hiepdph.calculatorvault.config.RepeatMode
import com.neko.hiepdph.calculatorvault.databinding.ActivityAudioPlayerBinding
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ActivityAudioPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding

    companion object {
        private var player: ExoPlayer? = null
    }

    private var isBound = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initView()
        initButton()
    }

    private fun initPlayer() {
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if(isPlaying){
                    binding.tvAudioNameFull.text = Objects.requireNonNull(
                        player!!.currentMediaItem
                    )?.mediaMetadata?.title
                    binding.progressDuration.text = getReadableTime(player!!.currentPosition.toInt())
                    binding.audioProgress.progress = player!!.currentPosition.toInt()
                    binding.totalDuration.text = getReadableTime(player!!.duration.toInt())
                    binding.audioProgress.max = player!!.duration.toInt()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
                    showCurrentArtwork()
                    updatePlayerPositionProgress()
                    binding.imvThumbCircle.animation = loadRotation()
                }
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.d("TAG", "onPlaybackStateChanged: " + playbackState)
                if (playbackState == ExoPlayer.STATE_READY) {
                    binding.tvAudioNameFull.text = Objects.requireNonNull(
                        player!!.currentMediaItem
                    )?.mediaMetadata?.title
                    binding.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
                    binding.progressDuration.text =
                        getReadableTime(player!!.currentPosition.toInt())
                    binding.audioProgress.progress = player!!.currentPosition.toInt()
                    binding.totalDuration.text = getReadableTime(player!!.duration.toInt())
                    binding.audioProgress.max = player!!.duration.toInt()
                    showCurrentArtwork()
                    updatePlayerPositionProgress()
                    binding.imvThumbCircle.animation = loadRotation()
                } else {
                    binding.btnPlayPause.setImageResource(R.drawable.ic_audio_play)
                    binding.imvThumbCircle.clearAnimation()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                binding.tvAudioNameFull.text = mediaItem?.mediaMetadata?.title
                player?.currentPosition?.let {
                    binding.audioProgress.progress = getReadableTime(it.toInt()).toInt()
                }
                player?.duration?.let {

                    binding.totalDuration.text = it.toInt().toString()
                    binding.audioProgress.max = it.toInt()
                }
                binding.btnPlayPause.setImageResource(R.drawable.ic_audio_play)
                showCurrentArtwork()
                updatePlayerPositionProgress()
                binding.imvThumbCircle.animation = loadRotation()
                if (!player!!.isPlaying) {
                    player!!.play()
                }
            }
        })
        //checking if the player is playing
        Log.d("TAG", "initPlayer: "+ player)
        Log.d("TAG", "initPlayer: "+ player?.isPlaying)
        if (player?.isPlaying == true) {
            binding.tvAudioNameFull.text = Objects.requireNonNull(
                player!!.currentMediaItem
            )?.mediaMetadata?.title
            binding.progressDuration.text = getReadableTime(player!!.currentPosition.toInt())
            binding.audioProgress.progress = player!!.currentPosition.toInt()
            binding.totalDuration.text = getReadableTime(player!!.duration.toInt())
            binding.audioProgress.max = player!!.duration.toInt()
            binding.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
            showCurrentArtwork()
            updatePlayerPositionProgress()
            binding.imvThumbCircle.animation = loadRotation()
        }
    }

    private fun loadRotation(): Animation? {
        val rotateAnimation = RotateAnimation(
            0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 10000
        rotateAnimation.repeatCount = Animation.INFINITE
        return rotateAnimation
    }

    private fun updatePlayerPositionProgress() {
        Handler().postDelayed({
            if (player!!.isPlaying) {
                binding.progressDuration.text = (getReadableTime(player!!.currentPosition.toInt()))
                binding.audioProgress.progress = player!!.currentPosition.toInt()
            }

            //repeat calling method
            updatePlayerPositionProgress()
        }, 1000)
    }

    private fun showCurrentArtwork() {
        binding.imvThumbCircle.setImageURI(
            Objects.requireNonNull(
                player!!.currentMediaItem
            )?.mediaMetadata?.artworkUri
        )
        binding.imvThumbFull.setImageURI(
            Objects.requireNonNull(
                player!!.currentMediaItem
            )?.mediaMetadata?.artworkUri
        )
        if (binding.imvThumbCircle.drawable == null) {
            binding.imvThumbCircle.setImageResource(R.drawable.ic_audio_detail_error)
            binding.imvThumbFull.setImageResource(0)
        }
    }

    private fun initView() {
        when (config.repeat) {
            RepeatMode.REPEAT_ALL -> {
                player?.repeatMode = Player.REPEAT_MODE_ALL
                binding.btnRepeat.setImageResource(R.drawable.ic_repeat)
            }
            RepeatMode.REPEAT_ONE -> {
                player?.repeatMode = Player.REPEAT_MODE_ONE
                binding.btnRepeat.setImageResource(R.drawable.ic_repeat_1)
            }
            else -> {
                player?.repeatMode = Player.REPEAT_MODE_OFF
                binding.btnRepeat.setImageResource(R.drawable.ic_shuffle)
            }
        }

    }

    private fun initButton() {


        binding.btnRepeat.clickWithDebounce {
            when (config.repeat) {
                RepeatMode.REPEAT_ALL -> {
                    player?.repeatMode = Player.REPEAT_MODE_ONE
                    config.repeat = RepeatMode.REPEAT_ONE
                    binding.btnRepeat.setImageResource(R.drawable.ic_repeat_1)
                }
                RepeatMode.REPEAT_ONE -> {
                    player?.repeatMode = Player.REPEAT_MODE_OFF
                    config.repeat = RepeatMode.SUFFLE
                    binding.btnRepeat.setImageResource(R.drawable.ic_shuffle)
                }
                else -> {
                    player?.repeatMode = Player.REPEAT_MODE_ALL
                    config.repeat = RepeatMode.REPEAT_ALL
                    binding.btnRepeat.setImageResource(R.drawable.ic_repeat)
                }
            }
        }
        binding.btnNext.clickWithDebounce {
            if (player?.hasNextMediaItem() == true) {
                player?.seekToNextMediaItem()
                showCurrentArtwork()
                updatePlayerPositionProgress()
                binding.imvThumbCircle.animation = loadRotation()
            }
        }
        binding.btnPlaylist.clickWithDebounce {}

        binding.btnPrev.clickWithDebounce {
            if (player?.hasPreviousMediaItem() == true) {
                player?.seekToPreviousMediaItem()
                showCurrentArtwork()
                updatePlayerPositionProgress()
                binding.imvThumbCircle.animation = loadRotation()
            }
        }
        binding.btnPlayPause.clickWithDebounce {
            if (player?.isPlaying == true) {
                player?.pause()
                binding.btnPlayPause.setImageResource(R.drawable.ic_audio_play)
                binding.imvThumbCircle.clearAnimation()
            } else {
                player?.play()
                binding.btnPlayPause.setImageResource(R.drawable.ic_audio_pause)
                binding.imvThumbCircle.clearAnimation()
            }
        }

        binding.audioProgress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressValue = 0
            override fun onProgressChanged(seekBar: SeekBar, p1: Int, p2: Boolean) {
                progressValue = seekBar.progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekBar.progress = progressValue
                binding.progressDuration.text = getReadableTime(progressValue)
                player?.seekTo(progressValue.toLong())
            }

        })
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeListAudio() {
        ShareData.getInstance().listItemAudio.observe(this) {
            if (it.isNotEmpty()) {
                setupPlayer(it.map { item ->
                    item.mPath
                })
            }
            initPlayer()

        }
    }

    private fun setupPlayer(listPath: List<String?>) {
        try {
            val listMediaItem = mutableListOf<MediaItem>()
            listPath.forEach {
                Log.d("TAG", "setupPlayer: " + it)
                listMediaItem.add(MediaItem.fromUri(it ?: ""))
            }
            Log.d("TAG", "setupPlayer: " + player)
            player?.setMediaItems(listMediaItem)
            player?.prepare()
            player?.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
//        if (player?.isPlaying == true) {
//            player?.stop()
//        }
//        player?.release()
        doUnbindService()
    }

    private fun doUnbindService() {
//        if (isBound) {
//            unbindService(playerServiceConnection)
//            isBound = false
//        }
    }
}