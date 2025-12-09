package com.kuemiin.animalfight.ui.fragment.splash

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.MobileAds
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.databinding.FragmentSplashBinding
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
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel by viewModels<SplashViewModel>()


    override fun isFullScreen(): Boolean = true

    override fun getLayoutId(): Int = R.layout.fragment_splash

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(requireContext()) {}
            MobileAds.setAppMuted(true)
        }
    }

    override fun setUp() {
        if (!requireActivity().isTaskRoot) {
            requireActivity().finish()
            return
        }
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(requireContext())
        if (!getBoolean(requireActivity(), "gatherStatus", false)) {
            googleMobileAdsConsentManager.gatherConsent(requireActivity()) { consentError ->
                if (consentError != null) { }
                MaxUtils.setBoolean(requireActivity(), "gatherStatus", true)

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
            requireActivity(), binding.bannerView, binding.shimmerContainerBanner,
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
                                    requireActivity(),
                                    MaxUtils.ISSHOWINGADSSPLASH,
                                    true
                                )
                            ) {
                                if (getBoolean(
                                        requireActivity(),
                                        MaxUtils.ISSHOWINGAOAFIRST,
                                        true
                                    )
                                ) {
                                    MaxUtils.showAdIfAvailableSplash(
                                        requireActivity(),
                                        object : MaxUtils.OnShowAdCompleteListener {
                                            override fun onShowAdComplete(isSuccess: Boolean) {
                                                viewModel.nextToHome()
                                            }
                                        })
                                } else {
                                    MaxUtils.showAdmobSplash(
                                        requireActivity(),
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
        MaxUtils.checkFromStart = false
        navigateTo(R.id.action_splashFragment_to_introFragment)
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }

    override fun onDestroy() {
        super.onDestroy()
        MaxUtils.checkFromStart = false
    }
}