package com.example.player.player

import com.example.player.player.bean.SongBean

/**
 * Created by ssk on 2022/4/23.
 */
interface IPlayer {
    fun setDataSource(songBean: SongBean)
    fun start()
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(position: Int)
}