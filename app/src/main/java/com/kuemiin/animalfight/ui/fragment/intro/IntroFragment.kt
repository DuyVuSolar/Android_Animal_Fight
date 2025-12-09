package com.kuemiin.animalfight.ui.fragment.intro

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.kuemiin.base.adapter.FragmentAdapter
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.databinding.FragmentIntroBinding
import com.kuemiin.animalfight.model.IntroSplash
import com.kuemiin.animalfight.ui.fragment.intro.fragment.IntroPageFragment
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.MaxUtils.getBoolean
import com.kuemiin.animalfight.utils.MaxUtils.isCheck
import com.kuemiin.animalfight.utils.MaxUtils.logFirebaseEvent
import com.kuemiin.animalfight.utils.extension.exhaustive
import com.kuemiin.animalfight.utils.extension.gone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class IntroFragment : BaseFragment<FragmentIntroBinding>(), IntroPageFragment.OnIntroActionListener {

    private var adapterFragment: FragmentAdapter? = null

    private val viewModel by viewModels<IntroViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_intro

    override fun setUp() {
        binding.viewmodel = viewModel
        binding.maxSingleton = MaxUtils
        setUpIntro()
        setUpEvents()
        lifecycleScope.launch {
            delay(3000L)
            binding.loadingLanguage.gone()
        }
    }

    private fun setUpIntro() {
        viewModel.getListIntroUse().forEach {
            viewModel.listFragment.add(IntroPageFragment.newInstance(it))
        }

        adapterFragment = FragmentAdapter(childFragmentManager, viewModel.listFragment)
        binding.vPagerIntroFragment.adapter = adapterFragment!!
        binding.vPagerIntroFragment.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position != viewModel.indexIntro.get()) {
                    viewModel.indexIntro.set(position)
                    requireContext().logFirebaseEvent("intro_$position")
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun setUpEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.introEvent.collect { event ->
                    when (event) {
                        IntroViewModel.IntroEvent.NavigateToHome -> {
                            startToHome()
                        }

                        IntroViewModel.IntroEvent.OnClickContinue -> {
                            try {
                                binding.vPagerIntroFragment.currentItem = viewModel.indexIntro.get()
                            } catch (e: Exception) {

                            }
                        }

                        IntroViewModel.IntroEvent.OnClickBackIntro -> {
                            try {
                                binding.vPagerIntroFragment.currentItem = viewModel.indexIntro.get()
                            } catch (e: Exception) {

                            }
                        }

                        IntroViewModel.IntroEvent.OnClickStartToHome -> {
                            requireContext().logFirebaseEvent("click_start_home_intro")
                            if (getBoolean(
                                    requireActivity(),
                                    MaxUtils.ISSHOWINGINTERINTRO,
                                    true
                                )
                            ) {
                                if (isCheck(requireActivity())) {
                                    MaxUtils.loadAdmobIntro(
                                        requireActivity(),
                                        object : MaxUtils.NextScreenListener {
                                            override fun nextScreen() {
                                                viewModel.navigateToHome()
                                            }
                                        })
                                } else {
                                    viewModel.navigateToHome()
                                }
                            } else {
                                viewModel.navigateToHome()
                            }
                        }
                    }.exhaustive
                }
            }
        }
    }

    private fun startToHome() {
        viewModel.setShowIntro()
        if (viewModel.showGuide.get()) {
            navigateTo(R.id.action_introFragment_to_mainFragment)
        } else {
            navigateTo(R.id.action_introFragment_to_guideFragment)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        viewModel.onClickBackIntro()
    }

    override fun onIntroFinished(resultData: IntroSplash?) {
        viewModel.onClickContinue()
    }

}