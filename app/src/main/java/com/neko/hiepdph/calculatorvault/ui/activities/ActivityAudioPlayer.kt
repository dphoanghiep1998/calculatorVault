package com.neko.hiepdph.calculatorvault.ui.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.neko.hiepdph.calculatorvault.databinding.ActivityAudioPlayerBinding
import com.neko.hiepdph.calculatorvault.service.MusicService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityAudioPlayer:AppCompatActivity() {
    private lateinit var binding : ActivityAudioPlayerBinding
    private var player:ExoPlayer?= null
    private var isBound = false

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doBindService()
    }

    private fun doBindService(){
        val playerIntent = Intent(this,MusicService::class.java)
        bindService(playerIntent,playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private val playerServiceConnection = object :ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as MusicService.ServiceBinder
            player = binder.getPlayerService().player
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(player?.isPlaying == true){
            player?.stop()
        }
        player?.release()
    }
}