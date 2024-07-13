package com.example.player.player.bean

import androidx.annotation.Keep


/**
 * Created by ssk on 2022/4/23.
 */
@Keep
data class SongDetailResult(val songs: List<SongBean>) : BaseResult()

@Keep
data class SongBean(
    //歌曲id
    val id: Long,
    //歌曲名称
    val name: String,
    val al: Al,
    val ar: List<Ar>,
)

@Keep
data class SongUrlBean(val data: List<SongUrl>)

@Keep
data class SongUrl(val url: String)