package com.example.center.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.ui.PlayerView
import com.example.center.IMusic
import com.example.center.R
import com.example.player.NCPlayer.mMediaPlayer
import com.example.center.receiver.MusicNotificationReceiver
import com.example.jetpackmvvm.util.logE
import com.example.utils.application.NCApplication
import java.io.IOException


/**
 * Created by ssk on 2022/4/26.
 */
@UnstableApi
class MusicPlayService : MediaSessionService() {

    private var mBinder: PlayerBinder? = null

    private var mediaSession:MediaSession?=null
    inner class PlayerBinder : IMusic.Stub() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun play() {
            try {
                //registerBroadcastReceiver()
                mMediaPlayer.reset()
                mMediaPlayer.setDataSource(
                    ""
                    //getAssets().openFd("roadsuntraveled.mp3").getFileDescriptor()
                )
                mMediaPlayer.prepareAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun pause() {
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
            }
        }

        override fun resume() {
            if (!mMediaPlayer.isPlaying) {
                mMediaPlayer.start()
            }
        }

        override fun stop() {
            mMediaPlayer.stop()
        }

        override fun release() {
            mMediaPlayer.release()
        }
    }


    private var mReceiver: MusicNotificationReceiver? = null

    companion object {
        const val ACTION_START_MUSIC_SERVICE = "ACTION_START_MUSIC_SERVICE"
        fun start() {
            val intent =  Intent(NCApplication.context, MusicPlayService::class.java).apply {
                action = ACTION_START_MUSIC_SERVICE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NCApplication.context.startForegroundService(intent)
            } else {
                NCApplication.context.startService(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        logE("onCreate")
        registerBroadcastReceiver()
        mBinder= PlayerBinder()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
        val notification = NotificationCompat.Builder(this, "play_control")
            // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_icon_radio)
            // Add media control buttons that invoke intents in your media service
//            .addAction(androidx.media3.session.R.drawable.media3_icon_previous, "Previous", prevPendingIntent) // #0
//            .addAction(androidx.media3.session.R.drawable.media3_icon_pause, "Pause", pausePendingIntent) // #1
//            .addAction(androidx.media3.session.R.drawable.media3_icon_next, "Next", nextPendingIntent) // #2
            // Apply the media style template.
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession!!)
                    .setShowActionsInCompactView(0,1,2 /* #1: pause button \*/))
            .setContentTitle("Wonderful music")
            .setContentText("My Awesome Band")
//            .setLargeIcon(albumArtBitmap)
            .build()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            if (it == ACTION_START_MUSIC_SERVICE) {
//                NCApplication.context.init {
//                    startForeground(MusicNotificationHelper.NOTIFICATION_ID, MusicNotificationHelper.getNotification())
//                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterBroadcastReceiver()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        logE("onBind")

        return super.onBind(intent)
//        return mBinder
    }
    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        //从最近任务中关闭Activity时，如果正在播放歌曲，则前台服务继续播放
//        if (!player.playWhenReady
//            || player.mediaItemCount == 0
//            || player.playbackState == Player.STATE_ENDED) {
//            // Stop the service if not playing, continue playing in the background
//            // otherwise.
//            stopSelf()
//        }
        //直接停止播放
        if (player.playWhenReady) {
            // Make sure the service is not in foreground.
            player.pause()
        }
        stopSelf()
    }
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = MusicNotificationReceiver()
            registerReceiver(mReceiver, IntentFilter().apply {
                addAction(MusicNotificationReceiver.ACTION_MUSIC_NOTIFICATION)
            },RECEIVER_EXPORTED)
        }
    }

    private fun unRegisterBroadcastReceiver() {
        mReceiver?.let {
            unregisterReceiver(it)
        }
    }
}