package com.kuemiin.animalfight.ui.fragment.result

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.databinding.FragmentResultBinding
import com.kuemiin.animalfight.model.EncoderModel
import com.kuemiin.animalfight.ui.activity.main.MainActivity
import com.kuemiin.animalfight.ui.dialog.ExitPreviewDialog
import com.kuemiin.animalfight.ui.fragment.main.MainFragment.Companion.isDownloadedRecent
import com.kuemiin.animalfight.utils.Constant
import com.kuemiin.animalfight.utils.FileUtils
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.MaxUtils.logFirebaseEvent
import com.kuemiin.animalfight.utils.extension.exhaustive
import com.kuemiin.animalfight.utils.extension.invisible
import com.kuemiin.animalfight.utils.gone
import com.kuemiin.animalfight.utils.visible
import com.kuemiin.animalfight.widgets.ui.FadeInThenOutAnimationEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("DEPRECATION")
@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>(){

    companion object {
        fun newBundle(encoder: EncoderModel) = bundleOf().apply {
            putParcelable(Constant.KEY_EXTRA_DATA, encoder)
        }
    }

    private var animationEffect: FadeInThenOutAnimationEffect? = null


    override fun isDarkTheme(): Boolean = false

    private val viewModel: ResultViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.fragment_result

    override fun setUp() {
        viewModel.encoderModel = arguments?.getParcelable(Constant.KEY_EXTRA_DATA) ?: return
        viewModel.typeFilter = viewModel.encoderModel.typeFilter
        viewModel.pathVideo = viewModel.encoderModel.pathVideo
        binding.viewmodel = viewModel
        viewModel.activity = requireActivity()
        setUpEvent()
        setUpView()

        viewModel.isLoadSuccess.set(true)
        viewModel.fileEncoder = viewModel.pathVideo
        setUpPlayVideo()
    }


    private fun setUpEvent() {
        lifecycleScope.launchWhenStarted {
            viewModel.homeEvent.collect { event ->
                when (event) {
                    ResultViewModel.Event.OnClickBack -> {
                        val back = {
                            viewModel.deleteVideo()
                            viewModel.pausePlayer()
                            viewModel.releaseAppPlayer()
                            MaxUtils.showAdsWithCustomCount(requireActivity(),
                                object : MaxUtils.NextScreenListener {
                                    override fun nextScreen() {
                                        viewModel.NavigateCamera()
                                    }
                                })
                        }
                        if (viewModel.isDownloading.get()) {
                            viewModel.isDownloading.set(false)
                            viewModel.progressDownload.set(0f)
                            return@collect
                        }

                        if (!viewModel.isSaved) {
                            ExitPreviewDialog(requireContext()).apply {
                                setUpData {
                                    File(viewModel.pathVideo).delete()
                                    back()
                                }
                                show()
                                viewModel.pausePlayer()
                            }
                            return@collect
                        }
                        back()
                    }

                    ResultViewModel.Event.NavigateGoToHome -> {
                        startActivity(MainActivity.newIntent(requireContext()))
                    }

                    ResultViewModel.Event.GoToHome -> {
                        goToHome()
                    }

                    ResultViewModel.Event.OnClickSave -> {
                        requireContext().logFirebaseEvent("click_video_preview_share")
                        
                        val saveDone = {
                            viewModel.isLoadSuccess.set(true)
                            isDownloadedRecent = true
                            binding.llSaved.visible()
                            binding.llSaved.gone(3000)

                            viewModel.addCollectionToDB {

                            }
                        }
                        saveDone()
                    }

                    ResultViewModel.Event.OnClickShare -> {
                        requireContext().logFirebaseEvent("click_video_preview_share")

                        FileUtils.shareVideo(requireContext(), File(viewModel.fileEncoder))
                    }

                    ResultViewModel.Event.OnClickPlayVideo -> {
                        animationEffect?.go(!viewModel.appPlayer.isPlaying())
                        if(viewModel.appPlayer.isPlaying()) requireContext().logFirebaseEvent("video_preview")
                        else{}
                    }

                    ResultViewModel.Event.LoadCompleted -> {
                        binding.playerView.visible(200)
                        binding.pbLoading.gone(200)
                        binding.imvClickPlayPause.visible(200)
                        animationEffect?.go(!viewModel.appPlayer.isPlaying())
                    }

                    ResultViewModel.Event.VideoEnd -> {
                        animationEffect?.go(!viewModel.appPlayer.isPlaying())
                    }

                    ResultViewModel.Event.LoadError -> {

                    }
                    ResultViewModel.Event.NavigateCamera -> {
                        goToHome()
                    }
                }.exhaustive
            }
        }
    }

    private fun setUpView() {
        animationEffect = FadeInThenOutAnimationEffect(binding.imvClickPlayPause)
    }

    private fun setUpPlayVideo() = viewModel.viewModelScope.launch {
        binding.playerView.invisible()
        binding.pbLoading.visible()
        delay(1000)
        binding.liveStatePlayer = viewModel.appPlayer.liveState
        viewModel.initVideo(binding.playerView)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun goToHome() {
        viewModel.deleteVideo()
        viewModel.pausePlayer()
        viewModel.releaseAppPlayer()

        MaxUtils.showAdsWithCustomCount(requireActivity(),
            object : MaxUtils.NextScreenListener {
                override fun nextScreen() {
                    viewModel.NavigateGoToHome()
                }
            })
    }

    override fun onBackPressed() {

    }
}