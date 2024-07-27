package com.example.utils.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Created by ssk on 2022/4/17.
 */
class NCApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        //KVCache.init(this)
        //MusicPlayService.start()
    }
}