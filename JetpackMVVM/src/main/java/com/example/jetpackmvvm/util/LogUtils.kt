package com.example.jetpackmvvm.util

import android.text.TextUtils
import android.util.Log
import me.hgj.jetpackmvvm.ext.util.jetpackMvvmLog

fun logW(string:String)=LogUtils.warnInfo(string)
fun logW(tag:String,string:String)=LogUtils.warnInfo(tag,string)
fun logE(string:String)=LogUtils.errorInfo(string)
fun logE(tag:String,string:String)=LogUtils.errorInfo(tag,string)
/**
 * 作者　: hegaojian
 * 时间　: 2020/3/26
 * 描述　:
 */
object LogUtils {
    /**
     * 唱跳rap，music~
     */
    private const val DEFAULT_TAG = "CTRMusic"
    fun debugInfo(tag: String?, msg: String?) {
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        Log.d(tag, msg!!)
    }

    fun debugInfo(msg: String?) {
        debugInfo(
            DEFAULT_TAG,
            msg
        )
    }

    fun warnInfo(tag: String?, msg: String?) {
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        Log.w(tag, msg!!)
    }

    fun warnInfo(msg: String?) {
        warnInfo(
            DEFAULT_TAG,
            msg
        )
    }

    fun errorInfo(msg: String?){
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
    }
    fun errorInfo(tag:String?,msg:String?){
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        errorInfo(
            DEFAULT_TAG,
            msg
        )
        Log.w(tag, msg!!)
    }

    /**
     * 这里使用自己分节的方式来输出足够长度的 message
     *
     * @param tag 标签
     * @param msg 日志内容
     */
    fun debugLongInfo(tag: String?, msg: String) {
        var msg = msg
        if (!jetpackMvvmLog || TextUtils.isEmpty(msg)) {
            return
        }
        msg = msg.trim { it <= ' ' }
        var index = 0
        val maxLength = 3500
        var sub: String
        while (index < msg.length) {
            sub = if (msg.length <= index + maxLength) {
                msg.substring(index)
            } else {
                msg.substring(index, index + maxLength)
            }
            index += maxLength
            Log.d(tag, sub.trim { it <= ' ' })
        }
    }

    fun debugLongInfo(msg: String) {
        debugLongInfo(
            DEFAULT_TAG,
            msg
        )
    }

}