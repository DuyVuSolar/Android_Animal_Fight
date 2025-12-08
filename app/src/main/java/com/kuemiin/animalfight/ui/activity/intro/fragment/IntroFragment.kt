package com.kuemiin.animalfight.ui.activity.intro.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.databinding.FragmentIntroBinding
import com.kuemiin.animalfight.model.IntroSplash
import com.kuemiin.animalfight.utils.Constant
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.isAndroid13
import dagger.hilt.android.AndroidEntryPoint

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class IntroFragment() : BaseFragment<FragmentIntroBinding>() {

    companion object {
        fun newInstance(data: IntroSplash) : IntroFragment {
            val args = Bundle().apply {
                putParcelable(Constant.KEY_EXTRA_DATA, data)
            }
            val fragment = IntroFragment()
            fragment.arguments = args
            return fragment
        }
    }


    private var mListener: OnIntroActionListener? = null
    private var data: IntroSplash ? = null

    override fun getLayoutId(): Int = R.layout.fragment_intro

    @Suppress("DEPRECATION")
    override fun setUp() {
        arguments?.let {
            data = if (isAndroid13()) {
                it.getParcelable(Constant.KEY_EXTRA_DATA, IntroSplash::class.java)!!
            } else {
                it.getParcelable(Constant.KEY_EXTRA_DATA)!!
            }
        }
        binding.item = data
        setUpEvent()
        setUpAdapter()
        data ?: return
        when (data!!.index) {
            0 -> {
                MaxUtils.loadNative123(
                    requireActivity(),
                    binding.flAdplaceholder,
                    binding.shimmerContainer,
                    binding.flAdplaceholder, 7
                )
            }

            5 -> {
                MaxUtils.loadNative123(
                    requireActivity(),
                    binding.flAdplaceholder,
                    binding.shimmerContainer,
                    binding.flAdplaceholder, 8
                )
            }

            3 -> {
                MaxUtils.loadNative123(
                    requireActivity(),
                    binding.flAdplaceholderFull,
                    binding.shimmerContainerFull,
                    binding.flAdplaceholderFull, 6
                )
            }
        }

        binding.cvNext.setOnClickListener {
            mListener?.onIntroFinished(data)
        }

        binding.cvNextIntro.setOnClickListener {
            mListener?.onIntroFinished(data)
        }
    }

    private fun setUpEvent() {

    }

    private fun setUpAdapter() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnIntroActionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null //lear memory
    }

    interface OnIntroActionListener {
        fun onIntroFinished(resultData: IntroSplash?)
    }


}