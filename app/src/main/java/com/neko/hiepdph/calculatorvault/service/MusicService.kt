package com.neko.hiepdph.calculatorvault.service

import android.app.Notification
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.ImageView
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityAudioPlayer
import java.util.Objects

class MusicService : Service() {
    private val serviceBinder = ServiceBinder()
    private var notificationManager: PlayerNotificationManager? = null
     var player: ExoPlayer? = null

    inner class ServiceBinder : Binder() {
        fun getPlayerService(): MusicService {
            return this@MusicService
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(applicationContext).build()
        val audioAttributes = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build()

        player?.setAudioAttributes(audioAttributes, true)

        val channelId = resources.getString(R.string.app_name) + "Music channel"
        val notificationId = 1233214

        notificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(descriptionAdapter).setChannelImportance(IMPORTANCE_HIGH)
            .setSmallIconResourceId(R.drawable.ic_small_screen)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setNextActionIconResourceId(R.drawable.ic_video_next)
            .setPreviousActionIconResourceId(R.drawable.ic_video_previous)
            .setPauseActionIconResourceId(R.drawable.ic_audio_pause)
            .setPlayActionIconResourceId(R.drawable.ic_audio_play)
            .setChannelNameResourceId(R.string.app_name).build()

        notificationManager?.apply {
            setPlayer(player)
            setPriority(PRIORITY_MAX)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
        }
    }

    //notification listener
    private val notificationListener = object :PlayerNotificationManager.NotificationListener{
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            super.onNotificationCancelled(notificationId, dismissedByUser)
            stopForeground(true)
            if(player?.isPlaying == true){
                player?.pause()
            }
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            super.onNotificationPosted(notificationId, notification, ongoing)
            startForeground(notificationId,notification)
        }
    }

    //adapter media
    private val descriptionAdapter = object : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return Objects.requireNonNull(player?.currentMediaItem)?.mediaMetadata?.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val intent = Intent(applicationContext, ActivityAudioPlayer::class.java)
            return PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return null
        }

        override fun getCurrentLargeIcon(
            player: Player, callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val imageView = ImageView(applicationContext)
            imageView.setImageURI(Objects.requireNonNull(player?.currentMediaItem?.mediaMetadata?.artworkUri))

            var bitmapDrawable = imageView.drawable as BitmapDrawable
            if (bitmapDrawable == null) {
                bitmapDrawable = ContextCompat.getDrawable(
                    applicationContext, R.drawable.ic_audio_detail_error
                ) as BitmapDrawable
            }
            return bitmapDrawable.bitmap
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(player?.isPlaying == true){
            player?.stop()
            notificationManager?.setPlayer(null)
            player?.release()
            player = null
            stopForeground(true)
            stopSelf()
        }
    }


}