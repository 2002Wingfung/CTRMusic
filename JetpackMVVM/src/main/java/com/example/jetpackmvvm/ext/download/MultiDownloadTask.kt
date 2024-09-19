package com.example.jetpackmvvm.ext.download

import com.example.jetpackmvvm.ext.util.logi
import com.example.jetpackmvvm.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Callback
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.net.URL
import java.nio.channels.FileChannel


/**
 * 构造函数
 *
 * @param start
 * 开始位置
 * @param end
 * 结束位置
 * @param file
 * 目标文件
 * @param loadUrl
 * 下载网址
 */
class MultiDownloadTask(
    private var start: Long,
    private val end: Long,
    private val file: File,
    private val loadUrl: String,
    private val listener: OnDownLoadListener,
    private val tag: String,
    private val index: Int,
    private val indexCount: Int
) : Runnable {
    override fun run() {
        var bufferedInputStream: BufferedInputStream? = null
        var randomAccessFile: RandomAccessFile? = null
        try {
            val responseBody = DownLoadManager.api.multiDownloadFile("bytes=$start-$end", loadUrl).execute().body()
            if (responseBody == null) {
                "responseBody is null".logi()
                listener.onDownLoadError(
                    key = tag,
                    throwable = Throwable("responseBody is null please check download url")
                )
                DownLoadPool.remove(tag)
                return
            }
            bufferedInputStream = BufferedInputStream(responseBody.byteStream())
            randomAccessFile = RandomAccessFile(file, "rw")

            // 源文件和目标文件的指针指向同一个位置
            //bufferedInputStream.skip(start)
            randomAccessFile.seek(start)

            val sourceLen = end - start
            var readLen = 0L
            val startIndex = start
            var lastProgress = 0
            // 如果比默认长度小，就没必要按照默认长度读取文件了
            val bs = ByteArray((if (2048 < sourceLen) 2048 else sourceLen).toInt())
//            logE("thread",Thread.currentThread().name+"start$start end $end")
//            logE("pointer",randomAccessFile.filePointer.toString())

            while ((bufferedInputStream.read(bs).also { readLen = it.toLong() }).toLong() != -1L) {
                start += readLen
                randomAccessFile.write(bs, 0, readLen.toInt())
                val progress = ((start-startIndex).toFloat() / sourceLen * 100).toInt() // 计算百分比
                if (lastProgress != progress) {
                    lastProgress = progress
                    //记录已经下载的长度
                    //ShareDownLoadUtil.putLong(tag, currentSaveLength)
                    listener.onMultiUpdate(
                        index,
                        tag,
                        progress,
                        start-startIndex,
                        readLen,
                        indexCount
                    )
//                    logE(Thread.currentThread().name, "start$start end$end")
                    if (start >= end-1) {
                        listener.onMultiDownLoadSuccess(tag, file.path, readLen,index,indexCount)
                    }

                }
            }



//            val inputStream = responseBody.byteStream()
//
//            val sourceLength=end - start
//            val accessFile = RandomAccessFile(file, "rw")
//            val channel = accessFile.channel
//            val mappedBuffer = channel.map(
//                FileChannel.MapMode.READ_WRITE,
//                start,
//                sourceLength
//            )
//            val buffer = ByteArray(1024 * 4)
//            var len = 0
//            var lastProgress = 0
//            var currentSaveLength = start //当前的长度
//
//            while (inputStream.read(buffer).also { len = it } != -1) {
//                mappedBuffer.put(buffer, 0, len)
//                currentSaveLength += len
//                logE("pointer",accessFile.filePointer.toString())
//                val progress = (currentSaveLength.toFloat() / sourceLength * 100).toInt() // 计算百分比
//                if (lastProgress != progress) {
//                    lastProgress = progress
//                    //记录已经下载的长度
////                    ShareDownLoadUtil.putLong(key, currentSaveLength)
////                    withContext(Dispatchers.Main) {
////                        loadListener.onUpdate(
////                            key,
////                            progress,
////                            currentSaveLength,
////                            fileLength,
////                            currentSaveLength == fileLength
////                        )
////                    }
//
////                    if (currentSaveLength == fileLength) {
////                        withContext(Dispatchers.Main) {
////                            loadListener.onDownLoadSuccess(key, filePath,fileLength)
////                        }
////                        DownLoadPool.remove(key)
////                    }
//                }
//            }
//
//            inputStream.close()
//            accessFile.close()
//            channel.close()
        } catch (e: Exception) {
            listener.onDownLoadError(tag,e)
            e.printStackTrace()
        } finally {
            //关闭流
            try {
                bufferedInputStream?.close()
                randomAccessFile?.close()
            } catch (e: IOException) {
                listener.onDownLoadError(tag,e)
                e.printStackTrace()
            }
        }
    }
}

class CheckContentLengthTask(
    private val url: String,
    private val loadListener: OnDownLoadListener,
    private val tag: String,
    private val lengthListener: (Long)->Unit
): Runnable{
    override fun run() {
        runCatching {
            val connection = URL(url).openConnection()
            val sourceSize = connection.contentLengthLong
            sourceSize
        }.onSuccess {
            lengthListener(it)
        }.onFailure {
            loadListener.onDownLoadError(tag,it)
        }
    }
}