package com.example.center.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.example.player.player.utils.KVCache

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
        KVCache.init(this)
        //MusicPlayService.start()
    }
}