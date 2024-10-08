package com.example.center.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.player.MusicPlayController
import com.example.utils.application.NCApplication

/**
 * Created by ssk on 2022/4/26.
 */
class MusicNotificationReceiver : BroadcastReceiver() {

    companion object {
        val ACTION_MUSIC_NOTIFICATION: String = NCApplication.context.packageName + ".NOTIFICATION_ACTIONS"
        const val KEY_EXTRA = "action_extra"
        const val ACTION_PLAY = "action_play"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PRE = "action_previous"
//        val ACTION_FAVOURITE = "action_favourite"
//        val ACTION_CLOSE = "action_close"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getStringExtra(KEY_EXTRA)?.run {
            when(this) {
                ACTION_PLAY -> {
                    if (MusicPlayController.isPlaying()) {
                        MusicPlayController.pause()
                    }else {
                        MusicPlayController.resume()
                    }
                }
                ACTION_NEXT -> {
                    MusicPlayController.playByRealIndex(MusicPlayController.getNextRealIndex())
                }
                ACTION_PRE -> {
                    MusicPlayController.playByRealIndex(MusicPlayController.getPreRealIndex())
                }
//                ACTION_FAVOURITE -> {
//
//                }
//                ACTION_CLOSE -> {
//
//                }
            }
        }
    }

}