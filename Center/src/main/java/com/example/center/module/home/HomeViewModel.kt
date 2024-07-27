package com.example.center.module.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.center.bean.BannerX
import com.example.center.jetpackmvvm.base.viewmodel.BaseViewModel
import com.example.jetpackmvvm.ext.request
import com.example.center.network.apiService
import me.hgj.jetpackmvvm.state.ResultState

class HomeViewModel : BaseViewModel() {


    private var _bannerList= MutableLiveData<ResultState<ArrayList<BannerX>>>()

    val bannerList: LiveData<ResultState<ArrayList<BannerX>>>
        get() = _bannerList


    fun requestBannerList(){
        request({ apiService.getBanner() },_bannerList)
    }
    fun requestPlayListData(){

    }
}