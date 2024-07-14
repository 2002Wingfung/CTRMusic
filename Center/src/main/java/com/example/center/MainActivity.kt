package com.example.center

import android.os.Bundle
import android.view.View
import com.example.center.databinding.ActivityMain2Binding
import com.example.center.jetpackmvvm.base.activity.BaseVmDbActivity
import com.example.center.jetpackmvvm.base.viewmodel.BaseViewModel
import com.example.center.ui.byeburgernavigationview.ByeBurgerBehavior
import com.example.jetpackmvvm.util.LogUtils



class MainActivity : BaseVmDbActivity<BaseViewModel, ActivityMain2Binding>() {
    inner class ProxyClick {
        private var num=1
        lateinit var titleBottomBar:com.example.center.databinding.MainAppBarBinding
        fun toMain() {
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

    override fun ActivityMain2Binding.initDataBindingView() {
        titleBottomBar.click = ProxyClick()
        titleBottomBar.click?.titleBottomBar=titleBottomBar
        navView.apply {
            layoutParams.width= resources.displayMetrics.widthPixels *4/ 5//屏幕的五分之四
            layoutParams = layoutParams
//            inflateHeaderView(R.layout.nav_header)
        }
//
//
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

    }

    override fun createObserver() {
//        TODO("Not yet implemented")
    }
}