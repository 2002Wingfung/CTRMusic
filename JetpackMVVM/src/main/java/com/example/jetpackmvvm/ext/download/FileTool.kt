package com.example.jetpackmvvm.ext.download

import com.example.jetpackmvvm.base.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.net.URL
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.util.concurrent.ThreadPoolExecutor


/**
 * @author : Hong Yongfeng
 * @date   : 2024/9/13
 */
object FileTool {

    //定义GB的计算常量
    private const val GB = 1024 * 1024 * 1024

    //定义MB的计算常量
    private const val MB = 1024 * 1024

    //定义KB的计算常量
    private const val KB = 1024

    /**
     * 下载文件到本地
     * @param key String
     * @param savePath String
     * @param saveName String
     * @param currentLength Long
     * @param responseBody ResponseBody
     * @param loadListener OnDownLoadListener
     */
    suspend fun downToFile(
        key: String,
        savePath: String,
        saveName: String,
        currentLength: Long,
        responseBody: ResponseBody,
        loadListener: OnDownLoadListener
    ) {
        val filePath = getFilePath(savePath, saveName)
        try {
            if (filePath == null) {
                withContext(Dispatchers.Main) {
                    loadListener.onDownLoadError(key, Throwable("mkdirs file [$savePath]  error"))
                }
                DownLoadPool.remove(key)
                return
            }
            //保存到文件
            saveToFile(currentLength, responseBody, filePath, key, loadListener)
        } catch (throwable: Throwable) {
            withContext(Dispatchers.Main) {
                loadListener.onDownLoadError(key, throwable)
            }
            DownLoadPool.remove(key)
        }
    }

    /**
     * @param currentLength Long
     * @param responseBody ResponseBody
     * @param filePath String
     * @param key String
     * @param loadListener OnDownLoadListener
     */
    private suspend fun saveToFile(
        currentLength: Long,
        responseBody: ResponseBody,
        filePath: String,
        key: String,
        loadListener: OnDownLoadListener
    ) {
        withContext(Dispatchers.IO){
            val fileLength = getFileLength(currentLength, responseBody)
            val inputStream = responseBody.byteStream()
            val accessFile = RandomAccessFile(File(filePath), "rw")
            val channel = accessFile.channel
            val mappedBuffer = channel.map(
                FileChannel.MapMode.READ_WRITE,
                currentLength,
                fileLength - currentLength
            )
            val buffer = ByteArray(1024 * 4)
            var len = 0
            var lastProgress = 0
            var currentSaveLength = currentLength //当前的长度

            while (inputStream.read(buffer).also { len = it } != -1) {
                mappedBuffer.put(buffer, 0, len)
                currentSaveLength += len

                val progress = (currentSaveLength.toFloat() / fileLength * 100).toInt() // 计算百分比
                if (lastProgress != progress) {
                    lastProgress = progress
                    //记录已经下载的长度
                    ShareDownLoadUtil.putLong(key, currentSaveLength)
                    withContext(Dispatchers.Main) {
                        loadListener.onUpdate(
                            key,
                            progress,
                            currentSaveLength,
                            fileLength,
                            currentSaveLength == fileLength
                        )
                    }

                    if (currentSaveLength == fileLength) {
                        withContext(Dispatchers.Main) {
                            loadListener.onDownLoadSuccess(key, filePath,fileLength)
                        }
                        DownLoadPool.remove(key)
                    }
                }
            }

            inputStream.close()
            accessFile.close()
            channel.close()
        }
    }

    /**
     * 数据总长度
     * @param currentLength Long
     * @param responseBody ResponseBody
     * @return Long
     */
    private fun getFileLength(
        currentLength: Long,
        responseBody: ResponseBody
    ) =
        if (currentLength == 0L) responseBody.contentLength() else currentLength + responseBody.contentLength()


    /**
     * 获取下载地址
     * @param savePath String
     * @param saveName String
     * @return String?
     */
    fun getFilePath(savePath: String, saveName: String): String? {
        if (!createFile(savePath)) {
            return null
        }
        return "$savePath/$saveName"
    }

    /**
     * 根据传入url获取最后一个 / 后的文件名
     * @param url
     * @return 返回文件名
     */
    fun getFileNameFromUrl(url: String) =
        url.substring(url.lastIndexOf("/") + 1)


    /**
     * 创建文件夹
     * @param downLoadPath String
     * @return Boolean
     */
    private fun createFile(downLoadPath: String): Boolean {
        val file = File(downLoadPath)
        if (!file.exists()) {
            return file.mkdirs()
        }
        return true
    }



    /**
     * 为目标文件分配空间
     *
     * @param targetFile
     * 目标文件
     * @param sourceSize
     * 源文件大小
     */
    fun openSpace(targetFile: File, sourceSize: Long) {
        var randomAccessFile: RandomAccessFile? = null
        try {
            randomAccessFile = RandomAccessFile(targetFile, "rw")
            randomAccessFile.setLength(sourceSize)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                randomAccessFile?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    /**
     * 格式化小数
     * @param bytes Long
     * @return String
     */
    fun bytes2kb(bytes: Long): String {
        val format = DecimalFormat("###.0")
        return when {
            bytes / GB >= 1 -> {
                format.format(bytes / GB) + "GB";
            }
            bytes / MB >= 1 -> {
                format.format(bytes / MB) + "MB";
            }
            bytes / KB >= 1 -> {
                format.format(bytes / KB) + "KB";
            }
            else -> {
                "${bytes}B";
            }
        }
    }

    /**
     * 获取App文件的根路径
     * @return String
     */
    fun getBasePath(): String {
        var p: String? = appContext.getExternalFilesDir(null)?.path
        val f: File? = appContext.getExternalFilesDir(null)
        if (null != f) {
            p = f.absolutePath
        }
        return p ?: ""
    }
}

