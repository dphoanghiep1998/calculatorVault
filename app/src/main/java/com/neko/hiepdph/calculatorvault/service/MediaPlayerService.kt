//package com.neko.hiepdph.calculatorvault.service
//
//import android.media.browse.MediaBrowser
//import android.os.Bundle
//import android.service.media.MediaBrowserService
//import android.support.v4.media.session.MediaSessionCompat
//import android.support.v4.media.session.PlaybackStateCompat
//
//class MediaPlayerService : MediaBrowserService() {
//    private var mediaSession: MediaSessionCompat? = null
//    private lateinit var stateBuilder: PlayerS
//    override fun onGetRoot(p0: String, p1: Int, p2: Bundle?): BrowserRoot? {
//        TODO("Not yet implemented")
//    }
//
//    override fun onLoadChildren(p0: String, p1: Result<MutableList<MediaBrowser.MediaItem>>) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        mediaSession = MediaSessionCompat(baseContext,"MEDIA_BROWSER").apply {
//            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//            stateBuilder = PlaybackStateCompat.Builder()
//                .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE).build()
//            setPlaybackState(stateBuilder)
//            setCallback(My)
//        }
//
//    }
//}