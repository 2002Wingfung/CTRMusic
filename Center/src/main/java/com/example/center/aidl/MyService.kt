package com.example.center.aidl

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View

class MyService: Service() {

//    class MyBinder :IMy.Stub(){
//        override fun getName(): String {
//            TODO("Not yet implemented")
//        }
//
//    }
    override fun onBind(intent: Intent?): IBinder? {
        Handler(Looper.getMainLooper()).post{

        }
        View(this).post{

        }
        val handler=MyHandler()
        return null
    }
    class MyHandler : Handler() {

    }
}