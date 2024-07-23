package com.example.center.module.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.center.R
import com.example.center.bean.BannerX
import com.example.center.databinding.FragmentHomeBinding
import com.example.center.jetpackmvvm.ext.parseState
import com.example.jetpackmvvm.base.fragment.BaseVmDbFragment
import com.example.jetpackmvvm.util.LogUtils
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import kotlinx.coroutines.launch
import me.hgj.jetpackmvvm.ext.util.dp2px

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : BaseVmDbFragment<HomeViewModel, FragmentHomeBinding>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val bannerList= arrayListOf(BannerX())

    val options =RequestOptions()/*.bitmapTransform(RoundedCorners(40))*/
        .transform(CenterCrop(),RoundedCorners(40))
        //.placeholder(R.drawable.bg_banner2)//图片加载出来前，显示的图片
        .fallback( R.drawable.bg_banner5) //url为空的时候,显示的图片
        .error(R.drawable.bg_banner7);//图片加载失败后，显示的图片

//    private val placeHolderList= arrayListOf(
//        R.drawable.bg_banner0,R.drawable.bg_banner1,R.drawable.bg_banner2,
//        R.drawable.bg_banner3,R.drawable.bg_banner4,R.drawable.bg_banner5,
//        R.drawable.bg_banner6,R.drawable.bg_banner7,R.drawable.bg_banner7,R.drawable.bg_banner7
//    )


    private lateinit var banner:Banner<Any,BannerAdapter<Any, *>>
    override fun initView(savedInstanceState: Bundle?) {
        banner = mDatabind.banner
        mDatabind.searchBarText="提示词"
        mDatabind.banner
            .addBannerLifecycleObserver(this)
            .setAdapter(object : BannerImageAdapter<BannerX>(bannerList) {
                override fun onBindView(
                    holder: BannerImageHolder?,
                    data: BannerX?,
                    position: Int,
                    size: Int
                ) {
                    if (holder != null) {
                        Glide.with(holder.itemView)
                            .load(data!!.imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .apply(options)
                            .into(holder.imageView)
                    }
                }
            })
            .setIndicator(CircleIndicator(context)).setBannerRound(30f)
        mDatabind.btnDrawer.setOnClickListener{
            mActivity.findViewById<DrawerLayout>(R.id.drawer_layout).open()
        }
    }

    override fun lazyLoadData() {

        //請求轮播图
        mViewModel.requestBannerList()
        mViewModel.requestPlayListData()
    }

    override fun createObserver() {
        mViewModel.bannerList.observe(viewLifecycleOwner){ resultState ->
            //resultState为null？
            parseState(resultState,{ it ->
                it.forEach { LogUtils.warnInfo(it.imageUrl) }
                banner.setDatas(it as List<BannerX>?)
            })
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                //在这里面写多个launch，每个launch对应一个collect，这样就不会相互影响了
                //缺点就是每次回调生命周期的时候都会加载一次数据
                

            }
        }
    }

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun  newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}