package com.example.jetpackmvvm.ext.download

import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ConcurrentHashMap

/**
 * @author : Hong Yongfeng
 * @date   : 2024/9/13
 */
fun downLoadExt(downloadResultState: MutableLiveData<DownloadResultState>): OnDownLoadListener {
    return object : OnDownLoadListener {
        private val multiArray by lazy { BooleanArray(64) }
        private val progressArray by lazy{ IntArray(64) }
        override fun onDownLoadPrepare(key: String) {
            //开始下载
            downloadResultState.postValue(DownloadResultState.onPending())
        }

        override fun onDownLoadError(key: String, throwable: Throwable) {
            //下载错误
            throwable.printStackTrace()
            downloadResultState.postValue(DownloadResultState.onError(throwable.message ?: "下载错误"))
        }

        override fun onDownLoadSuccess(key: String, path: String, size: Long) {
            //下载成功
            downloadResultState.postValue(DownloadResultState.onSuccess(path, size))
        }

        override fun onMultiDownLoadSuccess(key: String, path: String, size: Long, index: Int,indexCount:Int) {
            multiArray[index]=true
            for (i in 0 until indexCount){
                if (!multiArray[i]){
                    return
                }
            }
            //所有线程执行完毕
            downloadResultState.postValue(DownloadResultState.onSuccess(path, size))
            DownLoadPool.remove(key)
        }

        override fun onDownLoadPause(key: String) {
            //下载暂停
            downloadResultState.postValue(DownloadResultState.onPause())
        }

        override fun onUpdate(key: String, progress: Int, read: Long, count: Long, done: Boolean) {
            //正在下载
            downloadResultState.postValue(DownloadResultState.onProgress(read, count, progress))
        }

        /**
         * 不需要加权计算百分比，因为前面已经平均分过一次了,因此这样求得的progress近似于精确值
         * 此外不需要加锁，不需要做到绝对精确
         */
        override fun onMultiUpdate(
            index: Int,
            key: String,
            progress: Int,
            hadRead: Long,
            count: Long,
            indexCount: Int
        ) {
            progressArray[index] = progress
            var sum=0
            for (i in 0 until indexCount){
                sum+=progressArray[i]
            }
            downloadResultState.postValue(DownloadResultState.onProgress(hadRead, count, sum/indexCount))
        }
    }
}


