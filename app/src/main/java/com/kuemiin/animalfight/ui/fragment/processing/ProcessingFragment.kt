package com.kuemiin.animalfight.ui.fragment.processing

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.kuemiin.base.extension.show
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.databinding.FragmentProcessingBinding
import com.kuemiin.animalfight.model.EncoderModel
import com.kuemiin.animalfight.ui.activity.main.dpToPx
import com.kuemiin.animalfight.ui.dialog.ExitPreviewDialog
import com.kuemiin.animalfight.ui.fragment.result.ResultFragment
import com.kuemiin.animalfight.utils.Constant
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.MaxUtils.AD_UNIT_ID_NATIVE_COLLAP
import com.kuemiin.animalfight.utils.MaxUtils.ISCHECKCLICK
import com.kuemiin.animalfight.utils.MaxUtils.ISSHOWINGNATIVE_BOTTOM
import com.kuemiin.animalfight.utils.MaxUtils.getBoolean
import com.kuemiin.animalfight.utils.MaxUtils.logFirebaseEvent
import com.kuemiin.animalfight.utils.MaxUtils.logRevAdjust
import com.kuemiin.animalfight.utils.MaxUtils.logRevCC
import com.kuemiin.animalfight.utils.MaxUtils.populateUnifiedNativeAdView
import com.kuemiin.animalfight.utils.binding.setImageAnyNormal
import com.kuemiin.animalfight.utils.extension.exhaustive
import com.kuemiin.animalfight.utils.extension.gone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProcessingFragment : BaseFragment<FragmentProcessingBinding>(){

    companion object {
        fun newBundle(encoder: EncoderModel) = bundleOf().apply {
            putParcelable(Constant.KEY_EXTRA_DATA, encoder)
        }
    }

    override fun isDarkTheme(): Boolean = false

    private val viewModel: ProcessingViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_processing

    private fun registerNative() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            val topJob = async { loadNativeSuspend() }
//            val bottomJob = async { loadNativeBottomSuspend() }
//            awaitAll(topJob, bottomJob)
//        }
    }

    private var currentNativeAd: NativeAd? = null

    @SuppressLint("ClickableViewAccessibility")
    private suspend fun loadNativeSuspend(
        incrementCount: Boolean = true
    ): Unit = suspendCancellableCoroutine { cont ->
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {

            if (!getBoolean(requireActivity(), MaxUtils.ISSHOWINGNATIVE_COLLAP, true)) {
                binding.nativeCollap.gone()
                cont.resume(Unit)
                return@launch
            }

            if (!MaxUtils.isCheck(requireActivity())
            ) {
                binding.nativeCollap.gone()
                cont.resume(Unit)
                return@launch
            }

            binding.nativeCollap.show()

            binding.btnCollapsible.apply {
                visibility = View.GONE
                isClickable = false
                isFocusable = false
                isEnabled = false
                setOnTouchListener { _, _ -> false }
            }

            binding.btnCollapsible1.gone()

            binding.includeLayout.adGroup.removeAllViews()
            binding.includeLayout.root.apply {
                isClickable = false
                isFocusable = false
                isEnabled = false
            }

            binding.shimmerContainerNativeCollap.post {
                binding.shimmerContainerNativeCollap.show()
                binding.shimmerContainerNativeCollap.startShimmer()
            }

            val adLoader = AdLoader.Builder(requireActivity(), AD_UNIT_ID_NATIVE_COLLAP)
                .forNativeAd { unifiedNativeAd ->

                    currentNativeAd?.destroy()
                    currentNativeAd = unifiedNativeAd

                    binding.includeLayout.root.show()

                    val nativeAdView = layoutInflater.inflate(
                        R.layout.native_admob_ad_collap_trythis,
                        null
                    ) as NativeAdView

                    populateUnifiedNativeAdView(unifiedNativeAd, nativeAdView)

                    binding.includeLayout.adGroup.apply {
                        removeAllViews()
                        addView(nativeAdView)
                        isClickable = false
                        isFocusable = false
                        isEnabled = false
                        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                        setOnTouchListener { _, _ -> false }
                    }

                    binding.shimmerContainerNativeCollap.post {
                        binding.shimmerContainerNativeCollap.stopShimmer()
                        binding.shimmerContainerNativeCollap.setShimmer(null)
                        binding.shimmerContainerNativeCollap.gone()
                        binding.shimmerContainerNativeCollap.isClickable = false
                        binding.shimmerContainerNativeCollap.isFocusable = false
                        binding.shimmerContainerNativeCollap.isEnabled = false
                        binding.shimmerContainerNativeCollap.setOnTouchListener { _, _ -> false }
                    }

                    binding.btnCollapsible1.apply {
                        visibility = View.VISIBLE
                        isClickable = true
                        isFocusable = true
                        isEnabled = true
                        setOnClickListener {
                            binding.includeLayout.adGroup.removeAllViews()
                            binding.includeLayout.root.gone()
                            binding.nativeCollap.gone()
                        }
                    }

                    unifiedNativeAd.setOnPaidEventListener { impressionData ->
                        logRevAdjust(impressionData.valueMicros.toDouble())
                        logRevCC(requireActivity(), "native", impressionData.valueMicros.toDouble())
                    }

                    cont.resume(Unit)
                }
                .withAdListener(object : AdListener() {

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        binding.shimmerContainerNativeCollap.post {
                            binding.shimmerContainerNativeCollap.stopShimmer()
                            binding.shimmerContainerNativeCollap.gone()
                        }
                        binding.nativeCollap.gone()
                        cont.resume(Unit)
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        binding.btnCollapsible.show()
                        binding.btnCollapsible1.show()

                        if (MaxUtils.isCheck(requireActivity())) {
                            if (getBoolean(requireActivity(), ISCHECKCLICK, false)) {
                                if (MaxUtils.getCountClick(requireActivity()) % MaxUtils.getCount(requireActivity()) == 0) {
                                    binding.btnCollapsible1.apply {
                                        visibility = View.INVISIBLE
                                        isClickable = false
                                        isFocusable = false
                                        isEnabled = false
                                        setOnTouchListener { _, _ -> false }
                                    }
                                } else {
                                    binding.btnCollapsible1.updateLayoutParams<ViewGroup.LayoutParams> {
                                        width = dpToPx(requireActivity(), 20)
                                        height = dpToPx(requireActivity(), 20)
                                    }
                                    binding.btnCollapsible1.apply {
                                        visibility = View.VISIBLE
                                        isClickable = true
                                        isFocusable = true
                                        isEnabled = true
                                    }
                                }
                                if (incrementCount) MaxUtils.setCountClick(requireActivity())
                            } else {
                                binding.btnCollapsible1.updateLayoutParams<ViewGroup.LayoutParams> {
                                    width = dpToPx(requireActivity(), 20)
                                    height = dpToPx(requireActivity(), 20)
                                }
                                binding.btnCollapsible1.apply {
                                    visibility = View.VISIBLE
                                    isClickable = true
                                    isFocusable = true
                                    isEnabled = true
                                }
                            }
                        } else {
                            binding.btnCollapsible1.updateLayoutParams<ViewGroup.LayoutParams> {
                                width = dpToPx(requireActivity(), 20)
                                height = dpToPx(requireActivity(), 20)
                            }
                            binding.btnCollapsible1.apply {
                                visibility = View.VISIBLE
                                isClickable = true
                                isFocusable = true
                                isEnabled = true
                            }
                        }

                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        if (MaxUtils.getCountClick(requireActivity()) % MaxUtils.getCount(
                                requireActivity()
                            ) == 0
                        ) {
                            viewLifecycleOwner.lifecycleScope.launch {
                                loadNativeSuspend(incrementCount = false)
                            }
                        } else {
                            viewLifecycleOwner.lifecycleScope.launch {
                                loadNativeSuspend()
                            }
                        }
                        binding.btnCollapsible1.apply {
                            visibility = View.VISIBLE
                            isClickable = true
                            isFocusable = true
                            isEnabled = true
                        }
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setVideoOptions(VideoOptions.Builder().setStartMuted(true).build())
                        .build()
                )
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    private var currentNativeAdBottom: NativeAd? = null

    private suspend fun loadNativeBottomSuspend() = suspendCancellableCoroutine { cont ->
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {

            if (!getBoolean(requireActivity(), ISSHOWINGNATIVE_BOTTOM, true)) {
                binding.nativeCollapBanner.gone()
                binding.bannerViewBottom.gone()
                cont.resume(Unit)
                return@launch
            }

            binding.nativeCollapBanner.show()
            binding.bannerViewBottom.show()
            binding.btnCollapsibleBanner.gone()
            binding.includeLayoutBanner.adGroup.removeAllViews()
            binding.includeLayoutBannerBottom.adGroup.removeAllViews()

            binding.shimmerContainerNativeCollapBanner.post {
                binding.shimmerContainerNativeCollapBanner.show()
                binding.shimmerContainerNativeCollapBanner.startShimmer()
            }

            val adLoader = AdLoader.Builder(requireActivity(), MaxUtils.AD_UNIT_ID_NATIVE_BOTTOM)
                .forNativeAd { unifiedNativeAd ->
                    currentNativeAdBottom?.destroy()
                    currentNativeAdBottom = unifiedNativeAd

                    binding.btnCollapsibleBanner.show()
                    binding.includeLayoutBanner.root.show()
                    binding.includeLayoutBannerBottom.root.show()

                    binding.btnCollapsibleBanner.setOnClickListener {
                        binding.includeLayoutBanner.adGroup.removeAllViews()
                        binding.includeLayoutBanner.root.gone()
                        binding.nativeCollapBanner.gone()
                    }

                    unifiedNativeAd.setOnPaidEventListener { impressionData ->
                        logRevAdjust(impressionData.valueMicros.toDouble())
                        logRevCC(requireActivity(), "native", impressionData.valueMicros.toDouble())
                    }

                    binding.shimmerContainerNativeCollapBanner.post {
                        binding.shimmerContainerNativeCollapBanner.stopShimmer()
                        binding.shimmerContainerNativeCollapBanner.gone()
                    }

                    val layoutRes = R.layout.native_admob_ad_collap_trythis
                    val nativeAdView = layoutInflater.inflate(layoutRes, null) as NativeAdView
                    populateUnifiedNativeAdView(unifiedNativeAd, nativeAdView)
                    binding.includeLayoutBanner.adGroup.removeAllViews()
                    binding.includeLayoutBanner.adGroup.addView(nativeAdView)

                    val layoutResBanner = R.layout.native_banner_ad_trythis
                    val nativeAdViewBanner =
                        layoutInflater.inflate(layoutResBanner, null) as NativeAdView
                    MaxUtils.populateNativeAdViewBanner(unifiedNativeAd, nativeAdViewBanner)
                    binding.includeLayoutBannerBottom.adGroup.removeAllViews()
                    binding.includeLayoutBannerBottom.adGroup.addView(nativeAdViewBanner)

                    cont.resume(Unit)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        binding.shimmerContainerNativeCollapBanner.post {
                            binding.shimmerContainerNativeCollapBanner.stopShimmer()
                            binding.shimmerContainerNativeCollapBanner.gone()
                        }
                        binding.nativeCollapBanner.gone()
                        binding.bannerViewBottom.gone()
                        cont.resume(Unit)
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setVideoOptions(VideoOptions.Builder().setStartMuted(true).build())
                        .build()
                )
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    override fun setUp() {
        viewModel.encoderModel = arguments?.getParcelable(Constant.KEY_EXTRA_DATA) ?: return
        viewModel.pathVideo = viewModel.encoderModel.pathVideo
        binding.viewmodel = viewModel
        viewModel.activity = requireActivity()
        registerNative()
        setUpEvent()
        setUpView()
        requireContext().logFirebaseEvent("video_processing")
    }

    private fun setUpEvent() {
        lifecycleScope.launchWhenStarted {
            viewModel.homeEvent.collect { event ->
                when (event) {
                    ProcessingViewModel.Event.OnClickBack -> {
                        val back = {
                            viewModel.deleteVideo()
                            MaxUtils.showAdsWithCustomCount(requireActivity(),
                                object : MaxUtils.NextScreenListener {
                                    override fun nextScreen() {
                                        viewModel.NavigateHome()
                                    }
                                })
                        }
                        if (!viewModel.isDone) {
                            ExitPreviewDialog(requireContext()).apply {
                                setUpData {
                                    File(viewModel.pathVideo).delete()
                                    back()
                                }
                                show()
                            }
                            return@collect
                        }
                        back()
                    }

                    ProcessingViewModel.Event.GoToHome -> {
                        goToHome()
                    }

                    ProcessingViewModel.Event.NavigateHome -> {
                        navigateTo(R.id.action_processingFragment_to_homeFragment)
                    }

                    ProcessingViewModel.Event.NavigateResult -> {
                        navigateTo(
                            R.id.action_processingFragment_to_resultFragment,
                            ResultFragment.newBundle(viewModel.encoderModel)
                        )
                    }

                }.exhaustive
            }
        }
    }

    private fun setUpView() {
        binding.imvThumb.setImageAnyNormal(viewModel.pathVideo)
    }



    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        goToResult()
    }

    private fun goToResult() {
        if(!viewModel.isDone) return
        viewModel.NavigateResult()
    }

    private fun goToHome() {
        MaxUtils.showAdsWithCustomCount(requireActivity(),
            object : MaxUtils.NextScreenListener {
                override fun nextScreen() {
                    viewModel.NavigateHome()
                }
            })
    }

    override fun onBackPressed() {

    }
}