package com.example.center

import android.os.Bundle
import com.example.center.databinding.ActivityMain2Binding
import com.example.center.jetpackmvvm.base.activity.BaseVmDbActivity
import com.example.center.jetpackmvvm.base.viewmodel.BaseViewModel


class MainActivity : BaseVmDbActivity<BaseViewModel, ActivityMain2Binding>() {
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.navView.apply {
            layoutParams.width= resources.displayMetrics.widthPixels *4/ 5//屏幕的五分之四
            layoutParams = layoutParams
            inflateHeaderView(R.layout.nav_header)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

    }

    override fun createObserver() {
//        TODO("Not yet implemented")
    }
}
//class MainActivity :AppCompatActivity(){
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
//}