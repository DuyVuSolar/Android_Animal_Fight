package com.kuemiin.animalfight.ui.activity.intro

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
import com.kuemiin.animalfight.databinding.ActivityIntroBinding
import com.kuemiin.animalfight.model.IntroSplash
import com.kuemiin.animalfight.ui.activity.guide.GuideActivity
import com.kuemiin.animalfight.ui.activity.intro.fragment.IntroFragment
import com.kuemiin.animalfight.ui.activity.main.MainActivity
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
class IntroActivity : BaseActivity<ActivityIntroBinding>(), IntroFragment.OnIntroActionListener {

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, IntroActivity::class.java)
    }

    private var adapterFragment: FragmentAdapter? = null

    private val viewModel by viewModels<IntroViewModel>()

    override fun getLayoutId(): Int = R.layout.activity_intro

    override fun initViews() {
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
            viewModel.listFragment.add(IntroFragment.newInstance(it))
        }

        adapterFragment = FragmentAdapter(supportFragmentManager, viewModel.listFragment)
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
                    this@IntroActivity.logFirebaseEvent("intro_$position")
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
                            this@IntroActivity.logFirebaseEvent("click_start_home_intro")
                            if (getBoolean(
                                    this@IntroActivity,
                                    MaxUtils.ISSHOWINGINTERINTRO,
                                    true
                                )
                            ) {
                                if (isCheck(this@IntroActivity)) {
                                    MaxUtils.loadAdmobIntro(
                                        this@IntroActivity,
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
            startActivity(MainActivity.newIntent(this))
            finish()
        } else {
            startActivity(GuideActivity.newIntent(this))
            finish()
        }
    }

//    private fun startToHomeNative() {
//        viewModel.setShowIntro()
//        if (viewModel.showGuide.get()) {
//            startActivity(MainActivity.newIntent(this))
//            startActivity(Intent(this, MainActivity4::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
//            finish()
//        } else {
//            startActivity(GuideActivity.newIntent(this))
//            startActivity(Intent(this, MainActivity4::class.java).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
//            finish()
//        }
//    }

    override fun initEvents() {

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        viewModel.onClickBackIntro()
    }

    override fun onIntroFinished(resultData: IntroSplash?) {
        viewModel.onClickContinue()
    }

}