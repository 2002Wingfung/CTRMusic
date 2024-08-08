package com.example.center

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.RemoteException
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import com.example.center.databinding.ActivityMain2Binding
import com.example.center.databinding.MainAppBarBinding
import com.example.center.ext.initMain
import com.example.center.jetpackmvvm.base.viewmodel.BaseViewModel
import com.example.center.service.MusicPlayService
import com.example.center.ui.byeburgernavigationview.ByeBurgerBehavior
import com.example.jetpackmvvm.base.activity.BaseVmDbActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch


class MainActivity : BaseVmDbActivity<BaseViewModel, ActivityMain2Binding>() {
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
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bindService(Intent(this,MusicPlayService::class.java),connection,BIND_AUTO_CREATE)
    }

    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, MusicPlayService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                // Call controllerFuture.get() to retrieve the MediaController.
                // MediaController implements the Player interface, so it can be
                // attached to the PlayerView UI component.
                // playerView.setPlayer(controllerFuture.get())
                mediaController=controllerFuture.get()
                val mediaItem =
                    MediaItem.Builder()
                        .setMediaId("media-1")
                        .setUri("https://m701.music.126.net/20240808174637/17f1dd8c03ae7272d9737ec0a14b4d95/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/18763646424/2d92/5ecc/4cae/719e99af072fc4142d024fb7530fcd91.flac".toUri())
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setArtist("test")
                                .setTitle("Heros")
                                .setArtworkUri("https://p1.music.126.net/rFGNchd-J1L7dpv6n57Sxg==/7962663208587647.jpg".toUri())
                                .build()
                        )
                        .build()
                val mediaItem2 =
                    MediaItem.Builder()
                        .setMediaId("media-1")
                        .setUri("http://m801.music.126.net/20240808175840/ee62bacdd5c4d37eb4aa6ee052771424/jdymusic/obj/wo3DlMOGwrbDjj7DisKw/32071320177/3be8/2fd0/c7f3/276ef02f9e0cd6c9fd07a5c38988dbbc.flac".toUri())
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setArtist("Aimer")
                                .setTitle("TVアニメ「恋は雨上がりのように」EDテーマ")
                                .setArtworkUri("https://p1.music.126.net/wAxnnUZnkN7Soqf7nhjThQ==/109951166663296887.jpg".toUri())
                                .build()
                        )
                        .build()
                mediaController.setMediaItems(mutableListOf(mediaItem,mediaItem2))
            },
            /*MoreExecutors.directExecutor()*/
            ContextCompat.getMainExecutor(this)
        )

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

                            mediaController.prepare()
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
}