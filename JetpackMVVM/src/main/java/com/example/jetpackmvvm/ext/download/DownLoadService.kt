package com.example.jetpackmvvm.ext.download

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @author : Hong Yongfeng
 * @date   : 2024/9/13
 *
 */
interface DownLoadService {
    @Streaming
    @GET
    suspend fun downloadFile(
        @Header("RANGE") start: String,
        @Url url: String
    ): Response<ResponseBody>

    @Streaming
    @GET
    fun multiDownloadFile(
        @Header("RANGE") range: String,
        @Url url: String
    ): Response<ResponseBody>
}