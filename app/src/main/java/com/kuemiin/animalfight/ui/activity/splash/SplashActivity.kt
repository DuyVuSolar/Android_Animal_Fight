package com.kuemiin.animalfight.ui.activity.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.MobileAds
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseActivity
import com.kuemiin.animalfight.databinding.ActivitySplashBinding
import com.kuemiin.animalfight.ui.activity.intro.IntroActivity
import com.kuemiin.animalfight.utils.GoogleMobileAdsConsentManager
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.MaxUtils.getBoolean
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, SplashActivity::class.java).apply {
            }
    }

    private val viewModel by viewModels<SplashViewModel>()

    override fun getLayoutId(): Int = R.layout.activity_splash

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@SplashActivity) {}
            MobileAds.setAppMuted(true)
        }
    }

    override fun initViews() {
        if (!isTaskRoot) {
            finish()
            return
        }

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(this)
        if (!getBoolean(this, "gatherStatus", false)) {
            googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
                if (consentError != null) {
                }
                MaxUtils.setBoolean(this, "gatherStatus", true)

                if (googleMobileAdsConsentManager.canRequestAds) {
                    initializeMobileAdsSdk()
                    initView()
                }

            }
        } else {

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }

            initView()
        }
    }

    private fun initView() {
        MaxUtils.checkFromStart = true

        binding.viewmodel = viewModel
        binding.maxSingleton = MaxUtils

        MaxUtils.loadNativeSplash(
            this@SplashActivity, binding.bannerView, binding.shimmerContainerBanner,
            binding.flAdplaceholderBanner, object : MaxUtils.NextScreenListener {
                override fun nextScreen() {
                    viewModel.startDelayedNavigation()
                    viewModel.initialSeekbar()
                }
            }
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.splashEvent.collect { event ->
                    when (event) {
                        is SplashViewModel.SplashEvent.ShowAdsEvent -> {
                            if (getBoolean(
                                    this@SplashActivity,
                                    MaxUtils.ISSHOWINGADSSPLASH,
                                    true
                                )
                            ) {
                                if (getBoolean(
                                        this@SplashActivity,
                                        MaxUtils.ISSHOWINGAOAFIRST,
                                        true
                                    )
                                ) {
                                    MaxUtils.showAdIfAvailableSplash(
                                        this@SplashActivity,
                                        object : MaxUtils.OnShowAdCompleteListener {
                                            override fun onShowAdComplete(isSuccess: Boolean) {
                                                viewModel.nextToHome()
                                            }
                                        })
                                } else {
                                    MaxUtils.showAdmobSplash(
                                        this@SplashActivity,
                                        object : MaxUtils.OnShowAdCompleteListener {
                                            override fun onShowAdComplete(isSuccess: Boolean) {
                                                viewModel.nextToHome()
                                            }
                                        })
                                }
                            } else {
                                viewModel.nextToHome()
                            }
                        }

                        SplashViewModel.SplashEvent.FailedAOA -> {
                            viewModel.nextToHome()
                        }

                        SplashViewModel.SplashEvent.TimeOutAOA -> {
                            viewModel.nextToHome()
                        }

                        is SplashViewModel.SplashEvent.NavigateToHome -> {
                            navigateHome()
                        }

                        else -> {
                            // Handle other events
                        }
                    }
                }
            }
        }
    }

    private fun navigateHome() {
        startActivity(IntroActivity.newIntent(this@SplashActivity))
        MaxUtils.checkFromStart = false
        finish()
    }

    override fun initEvents() {
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }

    override fun onDestroy() {
        super.onDestroy()
        MaxUtils.checkFromStart = false
    }
}