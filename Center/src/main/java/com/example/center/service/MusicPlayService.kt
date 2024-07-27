package com.example.center.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.center.IMusic
import com.example.player.NCPlayer.mMediaPlayer
import com.example.center.receiver.MusicNotificationReceiver
import com.example.jetpackmvvm.util.logE
import com.example.utils.application.NCApplication
import java.io.IOException


/**
 * Created by ssk on 2022/4/26.
 */
class MusicPlayService : Service() {

    private var mBinder: PlayerBinder? = null
    class PlayerBinder : IMusic.Stub() {
        override fun play() {
            try {
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
        registerBroadcastReceiver()
        mBinder= PlayerBinder()

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
    }

    override fun onBind(intent: Intent?): IBinder? {
        logE("onBind")
        return mBinder
    }
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