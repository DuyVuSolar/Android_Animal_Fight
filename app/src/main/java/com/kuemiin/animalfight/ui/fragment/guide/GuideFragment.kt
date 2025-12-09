package com.kuemiin.animalfight.ui.fragment.guide

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.kuemiin.base.adapter.FragmentAdapter
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.databinding.FragmentGuideBinding
import com.kuemiin.animalfight.model.IntroSplash
import com.kuemiin.animalfight.ui.fragment.guide.fragment.GuidePageFragment
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.MaxUtils.getBoolean
import com.kuemiin.animalfight.utils.MaxUtils.logFirebaseEvent
import com.kuemiin.animalfight.utils.extension.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class GuideFragment : BaseFragment<FragmentGuideBinding>(), GuidePageFragment.OnGuideActionListener {

    private var adapterFragment: FragmentAdapter? = null

    private val viewModel by viewModels<GuideViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_guide

    override fun setUp() {
        binding.viewmodel = viewModel
        binding.maxSingleton = MaxUtils
        setUpIntro()
        setUpEvents()
        requireContext().logFirebaseEvent("permission_notification")
    }
    private fun setUpIntro() {
        viewModel.listGuide.forEach {
            viewModel.listFragment.add(GuidePageFragment.newInstance(it))
        }

        adapterFragment = FragmentAdapter(childFragmentManager, viewModel.listFragment)
        binding.vPagerGuideFragment.adapter = adapterFragment!!
        binding.vPagerGuideFragment.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position != viewModel.indexIntro.get()) {
                    viewModel.indexIntro.set(position)
                }
                if (position == 0) {
                    requireContext().logFirebaseEvent("permission_notification")
                } else {
                    requireContext().logFirebaseEvent("guide_screen")
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
                        GuideViewModel.IntroEvent.OnClickContinue -> {
                            try {
                                binding.vPagerGuideFragment.currentItem = viewModel.indexIntro.get()
                            } catch (e: Exception) {

                            }
                        }

                        GuideViewModel.IntroEvent.OnClickBackIntro -> {
                            try {
                                binding.vPagerGuideFragment.currentItem = viewModel.indexIntro.get()
                            } catch (e: Exception) {

                            }
                        }

                        GuideViewModel.IntroEvent.OnClickStartToHome -> {
                            requireContext().logFirebaseEvent("click_start_home_guide")
                            if (getBoolean(
                                    requireActivity(), MaxUtils.ISSHOWINGINTERGUIDE, true
                                )
                            ) {
                                MaxUtils.loadAdmobGuide(
                                    requireActivity(), object : MaxUtils.NextScreenListener {
                                        override fun nextScreen() {
                                            viewModel.navigateToHome()
                                        }
                                    })
                            } else {
                                viewModel.navigateToHome()
                            }
                        }

                        GuideViewModel.IntroEvent.NavigateToHome -> {
                            startToHome()
                        }
                    }.exhaustive
                }
            }
        }

    }

    private fun startToHome() {
        viewModel.setShowGuide()
        navigateTo(R.id.action_guideFragment_to_mainFragment)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        viewModel.onClickBackIntro()
    }

    override fun onGuideFinished(resultData: IntroSplash?) {
        viewModel.onClickContinue()
    }


}