package com.example.center.jetpackmvvm.network.manager

import com.example.center.jetpackmvvm.callback.livedata.event.EventLiveData
import me.hgj.jetpackmvvm.network.manager.NetState

/**
 * 作者　: hegaojian
 * 时间　: 2020/5/2
 * 描述　: 网络变化管理者
 */
class NetworkStateManager private constructor() {

    val mNetworkStateCallback = EventLiveData<NetState>()

    companion object {
        val instance: NetworkStateManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }

}