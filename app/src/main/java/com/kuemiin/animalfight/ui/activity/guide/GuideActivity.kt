package com.kuemiin.animalfight.ui.activity.guide

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.kuemiin.base.adapter.FragmentAdapter
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseActivity
import com.kuemiin.animalfight.databinding.ActivityGuideBinding
import com.kuemiin.animalfight.model.IntroSplash
import com.kuemiin.animalfight.ui.activity.guide.fragment.GuideFragment
import com.kuemiin.animalfight.ui.activity.main.MainActivity
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.MaxUtils.getBoolean
import com.kuemiin.animalfight.utils.MaxUtils.logFirebaseEvent
import com.kuemiin.animalfight.utils.extension.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class GuideActivity : BaseActivity<ActivityGuideBinding>(), GuideFragment.OnGuideActionListener {

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, GuideActivity::class.java)
    }

    private var adapterFragment: FragmentAdapter? = null

    private val viewModel by viewModels<GuideViewModel>()

    override fun getLayoutId(): Int = R.layout.activity_guide

    override fun initViews() {
        binding.viewmodel = viewModel
        binding.maxSingleton = MaxUtils
        setUpIntro()
        setUpEvents()
        logFirebaseEvent("permission_notification")

    }

    private fun setUpIntro() {
        viewModel.listGuide.forEach {
            viewModel.listFragment.add(GuideFragment.newInstance(it))
        }

        adapterFragment = FragmentAdapter(supportFragmentManager, viewModel.listFragment)
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
                    logFirebaseEvent("permission_notification")
                } else {
                    logFirebaseEvent("guide_screen")
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
                            this@GuideActivity.logFirebaseEvent("click_start_home_guide")
                            if (getBoolean(
                                    this@GuideActivity, MaxUtils.ISSHOWINGINTERGUIDE, true
                                )
                            ) {
                                MaxUtils.loadAdmobGuide(
                                    this@GuideActivity, object : MaxUtils.NextScreenListener {
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
        startActivity(MainActivity.newIntent(this))
        finish()
    }

//    private fun startToHomeGuide() {
//        viewModel.setShowGuide()
//        startActivity(MainActivity.newIntent(this))
//        if (getBoolean(this, ISSHOWINGNATIVE_FULLGUIDE, true)) {
//            if (isCheck(this)) {
//                startActivity(
//                    Intent(this, MainActivity2::class.java)
//                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                )
//            }
//        }
//        finish()
//    }

    override fun initEvents() {

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        viewModel.onClickBackIntro()
    }

    override fun onGuideFinished(resultData: IntroSplash?) {
        viewModel.onClickContinue()
    }


}