package com.example.center

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.RemoteException
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import com.example.center.databinding.ActivityMain2Binding
import com.example.center.databinding.MainAppBarBinding
import com.example.center.ext.initMain
import com.example.center.jetpackmvvm.base.viewmodel.BaseViewModel
import com.example.center.module.discovery.DiscoveryFragment
import com.example.center.service.MusicPlayService
import com.example.center.ui.byeburgernavigationview.ByeBurgerBehavior
import com.example.jetpackmvvm.base.activity.BaseVmDbActivity
import com.example.jetpackmvvm.ext.download.DownLoadManager
import com.example.jetpackmvvm.ext.download.DownloadResultState
import com.example.jetpackmvvm.ext.download.FileTool
import com.example.jetpackmvvm.ext.download.downLoadExt
import com.example.jetpackmvvm.util.logE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URL


class MainActivity : BaseVmDbActivity<BaseViewModel, ActivityMain2Binding>(), DiscoveryFragment.FragmentListener {
    private var myService: IMusic? = null
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            myService = IMusic.Stub.asInterface(service)
            try {
                service.linkToDeath(mDeathRecipient, 0)
            } catch (e: RemoteException) {
                throw RuntimeException(e)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }
    private val mDeathRecipient: DeathRecipient = object : DeathRecipient {
        override fun binderDied() {
            // TODO Auto-generated method stub
            if (myService == null) return
            myService!!.asBinder().unlinkToDeath(this, 0)
            myService = null
        }
    }

    inner class ProxyClick {
        private var num = 1
        lateinit var titleBottomBar: MainAppBarBinding
        fun toMain() {
//            mDatabind.titleBottomBar.bottomNavigationBar.setPadding(0,0,0,0)

            if (num % 2 == 0) ByeBurgerBehavior.from(titleBottomBar.bottomNavigationBar).hide()
            else ByeBurgerBehavior.from(titleBottomBar.bottomNavigationBar).show()
            num++
            titleBottomBar.button = "按钮"

//            finish()
//            //带点渐变动画
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun showLoading(message: String) {
//        TODO("Not yet implemented")
    }

    override fun dismissLoading() {

//        TODO("Not yet implemented")
    }

    override fun layoutId(): Int {
        return R.id.drawer_layout
    }

    override fun layoutView(): View {
        return mDatabind.ll
    }

    private var bottomBarHeight = 0
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var linearlayout: LinearLayout
    private var currentBottomId = R.id.home
    private lateinit var navController: NavController
    private lateinit var controllerFuture:ListenableFuture<MediaController>

    private lateinit var mediaController: MediaController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bindService(Intent(this,MusicPlayService::class.java),connection,BIND_AUTO_CREATE)
    }

    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, MusicPlayService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
//        controllerFuture.addListener(
//            {
//                // Call controllerFuture.get() to retrieve the MediaController.
//                // MediaController implements the Player interface, so it can be
//                // attached to the PlayerView UI component.
//                // playerView.setPlayer(controllerFuture.get())
//                mediaController=controllerFuture.get()
//                EventBus.getDefault().postSticky(mediaController)
////                PlayerView(this).player=mediaController
//                mediaController.addListener(object : Player.Listener {
//                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
//                        super.onMediaItemTransition(mediaItem, reason)
//                        logE("test"+mediaItem?.mediaMetadata?.artworkUri.toString())
//                    }
//                })
//                val mediaItem =
//                    MediaItem.Builder()
//                        .setMediaId("media-1")
//                        .setUri("https://m801.music.126.net/20240808191647/0602f860e11df2da2e22d8f3d2b93c2c/jdyyaac/obj/w5rDlsOJwrLDjj7CmsOj/14096574807/b79d/965c/96b1/ee690087dfcd2913a5dfac14e6ffcc5c.m4a".toUri())
//                        .setMediaMetadata(
//                            MediaMetadata.Builder()
//                                .setArtist("test")
//                                .setTitle("Heros")
//                                .setArtworkUri("https://p1.music.126.net/rFGNchd-J1L7dpv6n57Sxg==/7962663208587647.jpg".toUri())
//                                .build()
//                        )
//                        .build()
//                val mediaItem2 =
//                    MediaItem.Builder()
//                        .setMediaId("media-1")
//                        .setUri("https://m801.music.126.net/20240808191756/7a66db95764bc9748892776c0cc01c48/jdyyaac/obj/w5rDlsOJwrLDjj7CmsOj/14096427398/4f7e/6395/f2f4/b196096c1cdbb49b2a93e6d19942b1dc.m4a".toUri())
//                        .setMediaMetadata(
//                            MediaMetadata.Builder()
//                                .setArtist("Aimer")
//                                .setTitle("TVアニメ「恋は雨上がりのように」EDテーマ")
//                                .setArtworkUri("https://p1.music.126.net/wAxnnUZnkN7Soqf7nhjThQ==/109951166663296887.jpg".toUri())
//                                .build()
//                        )
//                        .build()
//                mediaController.setMediaItems(mutableListOf(mediaItem,mediaItem2))
//            },
//            /*MoreExecutors.directExecutor()*/
//            ContextCompat.getMainExecutor(this)
//        )

        val liveData=MutableLiveData<DownloadResultState>()
//        val url="https://upload.jianshu.io/users/upload_avatars/7687616/3ee6aef2-efc1-47a5-a868-0d1824d2a119.jpg?imageMogr2/auto-orient/strip|imageView2/1/w/80/h/80/format/webp"

        val url="https://nsub2t3.118pan.com/dl.php?NzU3ZTRlVWRHRVUrVUpJL0RqOFZTbGd2WkxVZUpTbURJS25NWUxHYkh6WTI5ZjZQTENsMGdsY2RZanJHNllZWFU2T2VpSDFmNjJldlJCSWszb1dCUmh5RmlFNFh2NC83aVRKOW9PcU8rT0Z5dkZDS2xUcHdYQ2c1L0ZPVG02OWhNQ28rSU1HN203emRZY2djN1g0SnBUS201Q1JLcVFMeUdQRVBJb0VKQ1ZvRUQ5NEZxT3hzVGJZNjVOcCt2ZkRCVE1MUEtsRlgzK0U1eitOK0x6WjZHSklPVDBnZ0s0NFMyZDFTdmFVT1FMUjhQWmFHN29pS3huKzkya3JDMW11azdJcTgza2hHN0hCUXRxaWxOdk5URGdIZWVBTWMrbVhkYmx5dExiWVpiZkRRUFRKVHlhbw%3D%3D"
//        lifecycleScope.launch(Dispatchers.IO) {
//            val connection = URL(url).openConnection()
//            val sourceSize = connection.contentLengthLong
//            logE("content-length",sourceSize.toString())
//            DownLoadManager.downLoad(
//                tag = "tag2",
//                url = url,
//                savePath = "/storage/emulated/0/Pictures/CTRMusic",
//                saveName = /*FileTool.getFileNameFromUrl(url)*/"3.mp3",
//                reDownload = true,
//                loadListener = downLoadExt(liveData)
//            )
//        }
        DownLoadManager.multiDownLoad(
            3,
            "tag5",
            url,
            "/storage/emulated/0/Pictures/CTRMusic",
            "5.mp3",
            true,
            loadListener = downLoadExt(liveData)
        )
        liveData.observe(this){
            when(it){
                is DownloadResultState.Error -> logE("livedata",it.errorMsg)
                DownloadResultState.Pause -> logE("livedata","pause")
                DownloadResultState.Pending -> logE("livedata","pending")
                is DownloadResultState.Progress -> logE("livedata","progress${it.progress}")
                is DownloadResultState.Success -> logE("livedata","success")
            }
        }
    }

    override fun ActivityMain2Binding.initDataBindingView() {
//        Picasso.get().load("").fit().into(ImageView(this@MainActivity))
        linearlayout = ll
//        navController=findNavController(R.id.host_fragment)
        mainViewpager.initMain(this@MainActivity)
        titleBottomBar.apply {
            click = ProxyClick()
            click?.titleBottomBar = titleBottomBar
            bottomNavigationView = bottomNavigationBar
            bottomNavigationBar.setOnItemSelectedListener {
                if (it.itemId != currentBottomId) {
//                    LogUtils.warnInfo(it.title.toString()+bottomBarHeight)
                    currentBottomId = it.itemId
                    when (it.itemId) {
                        R.id.home -> mainViewpager.setCurrentItem(0, false)
                        //navController.navigateAction(R.id.action_mine_fragment_to_home_fragment)
                        R.id.discovery -> {
                            mainViewpager.setCurrentItem(1, false)

//                            mediaController.prepare()
                            mediaController.play()
                        }
                        //navController.navigateAction(R.id.action_home_fragment_to_discovery_fragment)
                        R.id.mine -> mainViewpager.setCurrentItem(2, false)
                        //navController.navigateAction(R.id.action_home_fragment_to_mine_fragment)
                    }
                }
                true
            }
        }
        navView.apply {
            layoutParams.width = resources.displayMetrics.widthPixels * 4 / 5//屏幕的五分之四
            layoutParams = layoutParams
        }
    }

    override fun createObserver() {
    }

    override fun onStop() {
        super.onStop()
        MediaController.releaseFuture(controllerFuture)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        bottomBarHeight = mDatabind.titleBottomBar.bottomNavigationBar.height
//        LogUtils.warnInfo(bottomBarHeight.toString())
    }

    //歌单页面以及搜索页面为Fragment时才需要hide
    //如果是用Activity来实现，则不需要hide
    private fun hideBottomBar() {
        ByeBurgerBehavior.from(bottomNavigationView).hide()
        lifecycleScope.launch {
            delay(190)
            bottomNavigationView.visibility = View.GONE//一样的
//            linearlayout.setPadding(0,0,0,-bottomBarHeight)
        }
    }

    private fun showBottomBar() {
        ByeBurgerBehavior.from(bottomNavigationView).show()
        lifecycleScope.launch {
            delay(160)
            linearlayout.setPadding(0, 0, 0, bottomBarHeight)
        }
    }

    override fun myMediaController(): MediaController {
        return mediaController
    }
}