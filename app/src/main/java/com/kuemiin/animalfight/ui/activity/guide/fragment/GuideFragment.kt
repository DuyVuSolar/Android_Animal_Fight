package com.kuemiin.animalfight.ui.activity.guide.fragment

import android.content.Context
import android.os.Bundle
import com.kuemiin.base.extension.onDebounceClick
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.databinding.FragmentGuideBinding
import com.kuemiin.animalfight.model.IntroSplash
import com.kuemiin.animalfight.utils.Constant
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.PermissionUtils
import com.kuemiin.animalfight.utils.binding.setImageAnyNormal
import com.kuemiin.animalfight.utils.isAndroid13
import dagger.hilt.android.AndroidEntryPoint

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class GuideFragment() : BaseFragment<FragmentGuideBinding>() {

    var hasPerNotify = false

    companion object {
        fun newInstance(data: IntroSplash) : GuideFragment {
            val args = Bundle().apply {
                putParcelable(Constant.KEY_EXTRA_DATA, data)
            }
            val fragment = GuideFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var mListener: OnGuideActionListener? = null
    private var data: IntroSplash ? = null
    override fun getLayoutId(): Int = R.layout.fragment_guide

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
        setApplyView()
        data ?: return
        when (data!!.index) {
            3 -> {
                MaxUtils.loadNativeNotify(
                    requireActivity(),
                    binding.flAdplaceholder,
                    binding.shimmerContainer,
                    binding.flAdplaceholder
                )
            }

            4 -> {
                MaxUtils.loadNativeNotify(
                    requireActivity(),
                    binding.flAdplaceholder,
                    binding.shimmerContainer,
                    binding.flAdplaceholder, true
                )
            }
        }

        binding.cvNext.setOnClickListener {
            mListener?.onGuideFinished(data)
        }
    }

    private fun setUpEvent() {
        binding.imvSwNotify.onDebounceClick {
            if (hasPerNotify) return@onDebounceClick
            PermissionUtils.requestPermissionNotify(requireActivity(), {
                setApplyView()
            }, {
                setApplyView()
            })
        }
    }

    private fun setApplyView() {
        hasPerNotify = PermissionUtils.checkHasPerNotify(requireActivity())
        binding.imvSwNotify.setImageAnyNormal(if (hasPerNotify) R.drawable.ic_switch_on_green else R.drawable.ic_switch_off_gray)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGuideActionListener) {
            mListener = context
        }
    }
    interface OnGuideActionListener {
        fun onGuideFinished(resultData: IntroSplash?)
    }
}