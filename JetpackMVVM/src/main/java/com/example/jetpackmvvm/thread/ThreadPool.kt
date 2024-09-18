package com.example.jetpackmvvm.thread

import java.util.concurrent.BlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ThreadPool: (Int,Int,Long,BlockingQueue<Runnable>)->ThreadPool {

    private var corePoolSize=0
    private var maximumPoolSize=0
    private var keepAliveTime=0L
    private lateinit var workQueue: BlockingQueue<Runnable>
    private var firstSet=true

    private val executor by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue)
    }

    fun newThreadPool()= executor

    override fun invoke(corePoolSize: Int, maximumPoolSize: Int, keepAliveTime: Long, workQueue: BlockingQueue<Runnable>):ThreadPool {
        if (firstSet){
            this.corePoolSize=corePoolSize
            this.maximumPoolSize=maximumPoolSize
            this.keepAliveTime=keepAliveTime
            this.workQueue=workQueue
            firstSet=false
        }
        return this
    }
}