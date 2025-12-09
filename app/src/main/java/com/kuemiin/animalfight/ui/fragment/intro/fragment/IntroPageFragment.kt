package com.kuemiin.animalfight.ui.fragment.intro.fragment

import android.content.Context
import android.os.Bundle
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.databinding.FragmentIntroPageBinding
import com.kuemiin.animalfight.model.IntroSplash
import com.kuemiin.animalfight.utils.Constant
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.isAndroid13

class IntroPageFragment() : BaseFragment<FragmentIntroPageBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(data: IntroSplash) : IntroPageFragment {
            val args = Bundle().apply {
                putParcelable(Constant.KEY_EXTRA_DATA, data)
            }
            val fragment = IntroPageFragment()
            fragment.arguments = args
            return fragment
        }
    }


    private var mListener: OnIntroActionListener? = null
    private var data: IntroSplash ? = null

    override fun getLayoutId(): Int = R.layout.fragment_intro_page


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun setUp() {
        arguments?.let {
            data = if (isAndroid13()) {
                it.getParcelable(Constant.KEY_EXTRA_DATA, IntroSplash::class.java)!!
            } else {
                it.getParcelable(Constant.KEY_EXTRA_DATA)!!
            }
        }
        binding.item = data
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnIntroActionListener) {
            mListener = parentFragment as OnIntroActionListener?
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