package com.example.center

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import com.example.center.databinding.ActivityMain2Binding
import com.example.center.databinding.MainAppBarBinding
import com.example.jetpackmvvm.base.activity.BaseVmDbActivity
import com.example.center.jetpackmvvm.base.viewmodel.BaseViewModel
import com.example.center.ui.byeburgernavigationview.ByeBurgerBehavior
import com.example.jetpackmvvm.util.LogUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait


class MainActivity : BaseVmDbActivity<BaseViewModel, ActivityMain2Binding>() {
    inner class ProxyClick {
        private var num=1
        lateinit var titleBottomBar: MainAppBarBinding
        fun toMain() {
//            mDatabind.titleBottomBar.bottomNavigationBar.setPadding(0,0,0,0)

            if (num%2==0) ByeBurgerBehavior.from(titleBottomBar.bottomNavigationBar).hide()
            else ByeBurgerBehavior.from(titleBottomBar.bottomNavigationBar).show()
            num++
            titleBottomBar.button="按钮"

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
        return R.id.main
    }

    override fun layoutView(): View {
        return mDatabind.titleBottomBar.main
    }

    private var bottomBarHeight=0
    private lateinit var bottomNavigationView:BottomNavigationView
    private lateinit var linearlayout:LinearLayout
    override fun ActivityMain2Binding.initDataBindingView() {
        linearlayout=ll
        titleBottomBar.apply {
            click = ProxyClick()
            click?.titleBottomBar=titleBottomBar
            bottomNavigationView=bottomNavigationBar
            bottomNavigationBar.setOnItemSelectedListener {
//                LogUtils.warnInfo(it.title.toString()+bottomBarHeight)
                hideBottomBar()
                true
            }
        }
        navView.apply {
            layoutParams.width= resources.displayMetrics.widthPixels *4/ 5//屏幕的五分之四
            layoutParams = layoutParams
        }
    }
    override fun createObserver() {
//        TODO("Not yet implemented")
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        bottomBarHeight=mDatabind.titleBottomBar.bottomNavigationBar.height
//        LogUtils.warnInfo(bottomBarHeight.toString())
    }

    private fun hideBottomBar(){
        ByeBurgerBehavior.from(bottomNavigationView).hide()
        lifecycleScope.launch {
            delay(190)
            linearlayout.setPadding(0,0,0,-bottomBarHeight)
//            LogUtils.warnInfo(linearlayout.layoutParams.width.toString()+"")
//            LogUtils.warnInfo(linearlayout.layoutParams.height.toString()+"")
        }
    }
    private fun showBottomBar(){
        ByeBurgerBehavior.from(bottomNavigationView).show()
        lifecycleScope.launch {
            delay(160)
            linearlayout.setPadding(0,0,0,bottomBarHeight)
        }
    }
}