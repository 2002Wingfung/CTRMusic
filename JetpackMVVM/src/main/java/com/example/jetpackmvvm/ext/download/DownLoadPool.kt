package com.example.jetpackmvvm.ext.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import java.util.concurrent.ConcurrentHashMap


/**
 * @author : Hong Yongfeng
 * @date   : 2024/9/13
 */
object DownLoadPool {

    private val scopeMap: ConcurrentHashMap<String, CoroutineScope> = ConcurrentHashMap()

    private val currentSet=HashSet<String>()

    //下载位置
    private val pathMap: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    //监听
    private val listenerHashMap: ConcurrentHashMap<String, OnDownLoadListener> = ConcurrentHashMap()

    fun add(key: String, job: CoroutineScope) {
        scopeMap[key] = job
    }

    //监听
    fun add(key: String, loadListener: OnDownLoadListener) {
        listenerHashMap[key] = loadListener
    }

    //下载位置
    fun add(key: String, path: String) {
        pathMap[key] = path
    }

    //添加当前多线程tag
    fun add(key: String){
        currentSet.add(key)
    }


    fun remove(key: String) {
        pause(key)
        scopeMap.remove(key)
        listenerHashMap.remove(key)
        pathMap.remove(key)
        ShareDownLoadUtil.remove(key)
        currentSet.remove(key)
    }


    fun pause(key: String) {
        val scope = scopeMap[key]
        if (scope != null && scope.isActive) {
            scope.cancel()
        }
    }

    fun removeSetFromKey(key: String){
        currentSet.remove(key)
    }

    fun removeExitScope(key: String) {
        scopeMap.remove(key)
    }


    fun getScopeFromKey(key: String): CoroutineScope? {
        return scopeMap[key]
    }

    fun getListenerFromKey(key: String): OnDownLoadListener? {
        return listenerHashMap[key]
    }

    fun getPathFromKey(key: String): String? {
        return pathMap[key]
    }

    fun getListenerMap(): ConcurrentHashMap<String, OnDownLoadListener> {
        return listenerHashMap
    }
    fun getCurrentSet()= currentSet

}