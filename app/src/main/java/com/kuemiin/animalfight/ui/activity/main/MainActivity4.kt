package com.kuemiin.animalfight.ui.activity.main

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.kuemiin.base.extension.show
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.databinding.ActivityMain2Binding
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.MaxUtils.ISCHECKCLICK
import com.kuemiin.animalfight.utils.MaxUtils.ISCHECKCLICKINTRO
import com.kuemiin.animalfight.utils.MaxUtils.ISCHECKCLICKSPLASH
import com.kuemiin.animalfight.utils.MaxUtils.logRevAdjust
import com.kuemiin.animalfight.utils.MaxUtils.populateUnifiedNativeAdView
import com.kuemiin.animalfight.utils.extension.gone
import com.kuemiin.animalfight.utils.extension.isBuildLargerThan

@Suppress("DEPRECATION")
class MainActivity4 : AppCompatActivity() {
    protected lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)

        window?.apply {
            if (isBuildLargerThan(Build.VERSION_CODES.R)) {
                setDecorFitsSystemWindows(false)
                insetsController?.hide(WindowInsets.Type.statusBars())
                insetsController?.hide(WindowInsets.Type.navigationBars())
                insetsController?.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
            }
            this.statusBarColor = this.statusBarColor
        }
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
        MaxUtils.checkFromStart = true

        loadNative()
    }

    private fun loadNative(incrementCount: Boolean = true) {
        MaxUtils.checkFromStart = true

        binding.ivClose.apply {
            isClickable = false
            isFocusable = false
            setOnTouchListener { _, _ -> false }
        }
        binding.ivClose2.setOnClickListener {
            finish()
            MaxUtils.checkFromStart = false
        }

        binding.flAdplaceholder.visibility = View.VISIBLE
        binding.shimmerContainer.apply {
            visibility = View.VISIBLE
            startShimmer()
        }

        val adLoader = AdLoader.Builder(this, MaxUtils.AD_UNIT_ID_NATIVE_FULLINTRO)
            .forNativeAd { unifiedNativeAd ->
                unifiedNativeAd.setOnPaidEventListener { impressionData ->
                    MaxUtils.logRevCC(this, "native", impressionData.valueMicros.toDouble())
                    logRevAdjust(impressionData.valueMicros.toDouble())
                }
                binding.shimmerContainer.apply {
                    stopShimmer()
                    setShimmer(null)
                    isClickable = false
                    isFocusable = false
                    setOnTouchListener { _, _ -> false }
                    visibility = View.GONE
                }
                binding.flAdplaceholder.visibility = View.VISIBLE

                val nativeAdView =
                    this.layoutInflater.inflate(
                        R.layout.full_screen_native_ads,
                        null
                    ) as NativeAdView

                populateUnifiedNativeAdView(unifiedNativeAd, nativeAdView)
                binding.flAdplaceholder.removeView(binding.shimmerContainer)

                binding.flAdplaceholder.removeAllViews()
                binding.flAdplaceholder.addView(nativeAdView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    binding.shimmerContainer.apply {
                        stopShimmer()
                        visibility = View.GONE
                    }
                    binding.flAdplaceholder.visibility = View.GONE

                    finish()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    if (MaxUtils.getBoolean(this@MainActivity4, ISCHECKCLICK, false)) {
                        if (MaxUtils.getBoolean(this@MainActivity4, ISCHECKCLICKINTRO, false)) {

                            binding.ivClose.visibility = View.VISIBLE
                            binding.ivClose2.visibility = View.VISIBLE
                            if (MaxUtils.getCountClick(this@MainActivity4) % MaxUtils.getCount(this@MainActivity4) == 0) {
                                binding.ivClose2.visibility = View.INVISIBLE
                                binding.ivClose2.apply {
                                    isClickable = false
                                    isFocusable = false
                                    setOnTouchListener { _, _ -> false }
                                }
                            } else {
                                binding.ivClose2.updateLayoutParams<ViewGroup.LayoutParams> {
                                    width = dpToPx(this@MainActivity4, 24)
                                    height = dpToPx(this@MainActivity4, 24)
                                }
                            }

                            if (incrementCount) MaxUtils.setCountClick(this@MainActivity4)
                        } else {
                            binding.ivClose.visibility = View.VISIBLE
                            binding.ivClose2.visibility = View.VISIBLE
                            binding.ivClose2.updateLayoutParams<ViewGroup.LayoutParams> {
                                width = dpToPx(this@MainActivity4, 24)
                                height = dpToPx(this@MainActivity4, 24)
                            }
                        }
                    } else {
                        binding.ivClose.visibility = View.VISIBLE
                        binding.ivClose2.visibility = View.VISIBLE
                        binding.ivClose2.updateLayoutParams<ViewGroup.LayoutParams> {
                            width = dpToPx(this@MainActivity4, 24)
                            height = dpToPx(this@MainActivity4, 24)
                        }
                    }
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    if (MaxUtils.getCountClick(this@MainActivity4) % MaxUtils.getCount(this@MainActivity4) == 0) {
                        loadNative(true)
                    } else {
                        loadNative(false)
                    }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setVideoOptions(VideoOptions.Builder().setStartMuted(true).build()).build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
}