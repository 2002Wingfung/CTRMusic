package com.example.center.bean

import me.hgj.jetpackmvvm.network.BaseResponse


data class BannerX(
    val adDispatchJson: Any?=null,
    val adLocation: Any?=null,
    val adSource: Any?=null,
    val adid: Any?=null,
    val bannerBizType: String?=null,
    val encodeId: String?=null,
    val event: Any?=null,
    val exclusive: Boolean?=null,
    val extMonitor: Any?=null,
    val extMonitorInfo: Any?=null,
    val imageUrl: String?=null,
    val monitorBlackList: Any?=null,
    val monitorClick: Any?=null,
    val monitorClickList: Any?=null,
    val monitorImpress: Any?=null,
    val monitorImpressList: Any?=null,
    val monitorType: Any?=null,
    val program: Any?=null,
    val scm: String?=null,
    val song: Any?=null,
    val targetId: Long?=null,
    val targetType: Int?=null,
    val titleColor: String?=null,
    val typeTitle: String?=null,
    val url: String?=null,
    val video: Any?=null
)
data class Banner<T>(
    val banners: T,
    val code: Int
):BaseResponse<T>() {
    override fun isSuccess(): Boolean {
        return code==200
    }

    override fun getResponseData(): T {
        return banners
    }

    override fun getResponseCode(): Int {
        return code
    }

    override fun getResponseMsg(): String {
        return "null"
    }
}