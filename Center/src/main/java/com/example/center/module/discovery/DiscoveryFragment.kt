package com.example.center.module.discovery

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.example.center.R
import com.example.center.databinding.FragmentDiscoveryBinding
import com.example.jetpackmvvm.base.fragment.BaseVmDbFragment
import com.example.jetpackmvvm.util.logE
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiscoveryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiscoveryFragment : BaseVmDbFragment<DiscoveryViewModel,FragmentDiscoveryBinding>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener:FragmentListener?=null
    interface FragmentListener {
        //获取MediaController
        fun myMediaController():MediaController
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener=mActivity as FragmentListener
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        EventBus.getDefault().register(this)
    }

    @OptIn(UnstableApi::class)
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun getMediaController(controller: MediaController){
        mDatabind.playerView1.player=controller
        logE(controller.toString())
        //mDatabind.playerView.defaultArtwork=controller.
    }
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun lazyLoadData() {
    }

    override fun createObserver() {
    }

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiscoveryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiscoveryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}