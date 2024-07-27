package com.example.player.bean

import androidx.annotation.Keep
import java.io.Serializable

/**
 * Created by ssk on 2022/4/18.
 */

/**
 * 个人歌单
 */
@Keep
data class UserPlaylistResult(
    val playlist: List<com.example.player.bean.PlaylistBean>,
) : com.example.player.bean.BaseResult()

@Keep
data class PlaylistBean(
    val tracks: List<com.example.player.bean.Track>?,
    val trackIds: List<com.example.player.bean.TrackId>?,
    val creator: com.example.player.bean.Subscribers,
    val name: String = "",
    val coverImgUrl: String = "",
    val trackCount: Int = 0,
    val id: Long = 0,
    val playCount: Long = 0,
    val description: String?,
    val shareCount: Int,
    val commentCount: Int
)

@Keep
data class Subscribers(
    val userId: Long,
    val avatarUrl: String,
    val nickname: String
): Serializable

@Keep
data class Track(
    val name: String,
    val id: Long,
    val mv: Long,
    val ar: List<com.example.player.bean.Ar>,
    val al: com.example.player.bean.Al,
): Serializable

@Keep
data class TrackId(
    val id: Int = 0,
    val v: Int = 0,
    val alg: String? = null
): Serializable

@Keep
data class Ar(
    val id: Long,
    val name: String,
)

@Keep
data class Al(
    val id: Long,
    val name: String,
    val picUrl: String,
)