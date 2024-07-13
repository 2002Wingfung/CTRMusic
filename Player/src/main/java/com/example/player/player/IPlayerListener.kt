package com.ssk.ncmusic.core.player

import com.example.player.player.PlayerStatus

/**
 * Created by ssk on 2022/4/23.
 */
interface IPlayerListener {
    fun onStatusChanged(status: PlayerStatus)
    fun onProgress(totalDuring: Int, currentPosition: Int, percentage: Int)
}