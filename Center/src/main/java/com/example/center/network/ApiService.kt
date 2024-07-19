package com.example.center.network

import com.example.center.bean.ApiResponse
import com.example.center.bean.Banner
import com.example.center.bean.BannerX
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/23
 * 描述　: 网络API
 */
interface ApiService {

    companion object {
        const val BASE_URL = "https://music.bedofrosestll.cn/"
    }

    @GET("banner")
    suspend fun getBanner():Banner<ArrayList<BannerX>>

}