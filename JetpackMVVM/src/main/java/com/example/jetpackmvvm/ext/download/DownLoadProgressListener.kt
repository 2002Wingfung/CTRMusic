package com.example.jetpackmvvm.ext.download

/**
 * @author : Hong Yongfeng
 * @date   : 2024/9/13
 */
interface DownLoadProgressListener {

    /**
     * 下载进度
     * @param key url
     * @param progress  进度
     * @param read  读取
     * @param count 总共长度
     * @param done  是否完成
     */
    fun onUpdate(key: String,progress: Int, read: Long,count: Long,done: Boolean)

    fun onMultiUpdate(index:Int, key: String, progress: Int,hadRead: Long,count: Long,indexCount: Int)
}


interface OnDownLoadListener : DownLoadProgressListener {

    //等待下载
    fun onDownLoadPrepare(key: String)

    //下载失败
    fun onDownLoadError(key: String, throwable: Throwable)

    //下载成功
    fun onDownLoadSuccess(key: String, path: String,size:Long)

    //多线程下载成功
    fun onMultiDownLoadSuccess(key: String,path: String, currentLength: Long,index: Int,indexCount:Int)

    //下载暂停
    fun onDownLoadPause(key: String)
}

interface ContentLengthListener{
    fun updateContentLength(length: Long)
}