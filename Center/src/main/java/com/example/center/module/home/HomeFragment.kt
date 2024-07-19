package com.example.center.module.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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

    private val bannerList= arrayListOf<BannerX>()

    private lateinit var banner:Banner<Any,BannerAdapter<Any, *>>
    override fun initView(savedInstanceState: Bundle?) {
        banner = mDatabind.banner
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
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                            .into(holder.imageView)
                    }
                }
            })
            .setIndicator(CircleIndicator(context))
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
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}