package com.example.center

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.RemoteException
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.center.databinding.ActivityMain2Binding
import com.example.center.databinding.MainAppBarBinding
import com.example.center.ext.initMain
import com.example.center.jetpackmvvm.base.viewmodel.BaseViewModel
import com.example.center.service.MusicPlayService
import com.example.center.ui.byeburgernavigationview.ByeBurgerBehavior
import com.example.jetpackmvvm.base.activity.BaseVmDbActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService(Intent(this,MusicPlayService::class.java),connection,BIND_AUTO_CREATE)

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
                        R.id.discovery -> mainViewpager.setCurrentItem(1, false)
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