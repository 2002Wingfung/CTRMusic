package com.example.jetpackmvvm.ext.download

import android.os.Looper
import com.example.jetpackmvvm.ext.download.FileTool.getFilePath
import com.example.jetpackmvvm.ext.download.FileTool.openSpace
import com.example.jetpackmvvm.ext.util.logi
import com.example.jetpackmvvm.thread.ThreadPool
import com.example.jetpackmvvm.util.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 * @author : Hong Yongfeng
 * @date   : 2024/9/13
 */
object DownLoadManager {
    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS).build()
            ).build()
    }
    val api: DownLoadService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        retrofitBuilder.create(DownLoadService::class.java)
    }

    private val blockingQueue=SynchronousQueue<Runnable>()

    fun multiDownLoad(
        threadNum: Int,
        tag: String,
        url: String,
        savePath: String,
        saveName: String,
        reDownload: Boolean = false,
        loadListener: OnDownLoadListener
    ){
        if (DownLoadPool.getCurrentSet().contains(tag)){
            "已经在队列中".logi()
            return
        }

        if (saveName.isEmpty()) {
            //传入的文件名为空串
            loadListener.onDownLoadError(tag, Throwable("save name is Empty"))
            return
        }

        if (Looper.getMainLooper().thread != Thread.currentThread()) {
            //判断当前线程是否为主线程
            loadListener.onDownLoadError(tag, Throwable("current thread is not in main thread"))
            return
        }
        //在主线程初始化线程池，不需要线程同步
        val threadPool = ThreadPool(
            corePoolSize = 5,
            maximumPoolSize = 64,
            keepAliveTime = 60L,
            workQueue = blockingQueue
        ).newThreadPool()
        //在主线程进行加入哈希表，不用处理线程同步问题
        DownLoadPool.add(tag)
        threadPool.execute{
            runCatching {
                val connection = URL(url).openConnection()
//                connection as HttpURLConnection
//                connection.disconnect()
                connection.contentLengthLong
            }.onSuccess {
                val file = File("$savePath/$saveName")
                val currentLength = if (!file.exists()) {
                    //文件不存在，则从头开始下载
                    0L
                } else {
                    //文件存在，则读取已下载的长度
                    ShareDownLoadUtil.getLong(tag, 0)
                }
                if (file.exists()&&currentLength == 0L && !reDownload) {
                    //文件已存在了，且上一次已经下载完，且没有设置重新下载
                    loadListener.onDownLoadSuccess(tag, file.path, file.length())
                }else{
                    "startDownLoad current $currentLength".logi()
                    DownLoadPool.add(tag, "$savePath/$saveName")
                    DownLoadPool.add(tag, loadListener)
                    loadListener.onDownLoadPrepare(key = tag)
                    startLoadThread(threadNum,tag,it,savePath,saveName,url,threadPool,loadListener,currentLength)
                }
            }.onFailure {
                loadListener.onDownLoadError(tag,it)
            }
        }
    }

    /**
     * 如果出现不整除的情况(如:11字节,4个线程,每个线程3字节,多出1字节)，但是实际上RandomAccessFile的read()
     * 读到文件尾会返回-1,因此不考虑余数问题
     *
     * @param threadNum
     * 线程数量
     * @param sourceURL
     * 源文件URL
     */
    private fun startLoadThread(
        threadNum: Int,
        tag: String,
        sourceSize: Long,
        savePath: String,
        saveName: String,
        sourceURL: String,
        pool: ThreadPoolExecutor,
        listener: OnDownLoadListener,
        currentLength: Long = 0,
        continueDownload: Boolean = false
    ) {
        try {
            val filePath = getFilePath(savePath, saveName)
            if (filePath == null) {
                listener.onDownLoadError(tag, Throwable("mkdirs file [$savePath]  error"))
                DownLoadPool.remove(tag)
                return
            }
            val file = File(filePath)
            if (continueDownload){
                //断点续传+多线程下载
            }else{
                // 为目标文件分配空间，只需要在第一次下载时分配空间
                openSpace(file, sourceSize)
                // 分线程下载文件
//                val avgSize = sourceSize / threadNum + 1
//                for (i in 0 until threadNum) {
//                    val end=(avgSize * (i + 1)).coerceAtMost(sourceSize-1)
//                    println((avgSize * i).toString() + "------" + end)
//                    pool.execute(
//                        MultiDownloadTask(
//                            avgSize * i, end,
//                            file, sourceURL,listener,tag,i,threadNum
//                        )
//                    )
//                }
            }
            val avgSize = (sourceSize-currentLength) / threadNum + 1
            for (i in 0 until threadNum) {
                val end = (avgSize * (i + 1) + currentLength).coerceAtMost(sourceSize-1)
                println((avgSize * i  + currentLength).toString() + "------" + end)
                pool.execute(
                    MultiDownloadTask(
                        avgSize * i + currentLength, end,
                        file, sourceURL,listener,tag,i,threadNum
                    )
                )
            }
        } catch (e: Exception) {
            listener.onDownLoadError(tag,e)
            e.printStackTrace()
        }
    }
    /**
     *开始下载
     * @param tag String 标识
     * @param url String 下载的url
     * @param savePath String 保存的路径
     * @param saveName String 保存的名字
     * @param reDownload Boolean 如果文件已存在是否需要重新下载 默认不需要重新下载
     * @param loadListener OnDownLoadListener
     */
    suspend fun downLoad(
        tag: String,
        url: String,
        savePath: String,
        saveName: String,
        reDownload: Boolean = false,
        loadListener: OnDownLoadListener
    ) {
        withContext(Dispatchers.IO) {
            doDownLoad(tag, url, savePath, saveName, reDownload, loadListener, this)
        }
    }

    /**
     * 取消下载
     * @param key String 取消的标识
     */
    private fun cancel(key: String) {
        val path = DownLoadPool.getPathFromKey(key)//根据key获取文件路径
        if (path != null) {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }
        DownLoadPool.remove(key)
    }

    /**
     * 暂停下载
     * @param key String 暂停的标识
     */
    private fun pause(key: String) {
        val listener = DownLoadPool.getListenerFromKey(key)
        listener?.onDownLoadPause(key)
        DownLoadPool.pause(key)
    }

    /**
     * 取消所有下载
     */
    fun doDownLoadCancelAll() {
        DownLoadPool.getListenerMap().forEach {
            cancel(it.key)
        }
    }

    /**
     * 暂停所有下载
     */
    fun doDownLoadPauseAll() {
        DownLoadPool.getListenerMap().forEach {
            pause(it.key)
        }
    }

    /**
     *下载
     * @param tag String 标识
     * @param url String 下载的url
     * @param savePath String 保存的路径
     * @param saveName String 保存的名字
     * @param reDownload Boolean 如果文件已存在是否需要重新下载 默认不需要重新下载
     * @param loadListener OnDownLoadListener
     * @param coroutineScope CoroutineScope 上下文
     */
    private suspend fun doDownLoad(
        tag: String,
        url: String,
        savePath: String,
        saveName: String,
        reDownload: Boolean,
        loadListener: OnDownLoadListener,
        coroutineScope: CoroutineScope
    ) {
        //判断是否已经在队列中
        val scope = DownLoadPool.getScopeFromKey(tag)
        if (scope != null && scope.isActive) {
            "已经在队列中".logi()
            return
        } else if (scope != null && !scope.isActive) {
            "key $tag 已经在队列中 但是已经不再活跃 remove".logi()
            DownLoadPool.removeExitScope(tag)
        }

        if (saveName.isEmpty()) {
            //传入的文件名为空串
            withContext(Dispatchers.Main) {
                loadListener.onDownLoadError(tag, Throwable("save name is Empty"))
            }
            return
        }

        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            //判断当前线程是否为主线程
            withContext(Dispatchers.Main) {
                loadListener.onDownLoadError(tag, Throwable("current thread is in main thread"))
            }
            return
        }

        val file = File("$savePath/$saveName")
        val currentLength = if (!file.exists()) {
            //文件不存在，则从头开始下载
            0L
        } else {
            //文件存在，则读取已下载的长度
            ShareDownLoadUtil.getLong(tag, 0)
        }
        if (file.exists()&&currentLength == 0L && !reDownload) {
            //文件已存在了，且上一次已经下载完，且没有设置重新下载
            loadListener.onDownLoadSuccess(tag, file.path, file.length())
            return
        }
        "startDownLoad current $currentLength".logi()

        try {
            //添加到pool
            DownLoadPool.add(tag, coroutineScope)
            DownLoadPool.add(tag, "$savePath/$saveName")
            DownLoadPool.add(tag, loadListener)

            withContext(Dispatchers.Main) {
                //准备开始下载
                loadListener.onDownLoadPrepare(key = tag)
            }
            val response = api.downloadFile("bytes=$currentLength-", url)
            val responseBody = response.body()
            if (responseBody == null) {
                "responseBody is null".logi()
                withContext(Dispatchers.Main) {
                    loadListener.onDownLoadError(
                        key = tag,
                        throwable = Throwable("responseBody is null please check download url")
                    )
                }
                DownLoadPool.remove(tag)
                return
            }
            FileTool.downToFile(
                tag,
                savePath,
                saveName,
                currentLength,
                responseBody,
                loadListener
            )
        } catch (throwable: Throwable) {
            withContext(Dispatchers.Main) {
                loadListener.onDownLoadError(key = tag, throwable = throwable)
            }
            DownLoadPool.remove(tag)
        }
    }
}


