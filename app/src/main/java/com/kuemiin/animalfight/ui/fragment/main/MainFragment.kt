@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.kuemiin.animalfight.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lhd.visualizer_record.VisualizerRecorder
import com.kuemiin.animalfight.databinding.FragmentMainBinding
import com.kuemiin.animalfight.utils.Constant
import com.kuemiin.animalfight.utils.extension.exhaustive
import com.kuemiin.animalfight.utils.extension.toast
import kotlinx.coroutines.launch
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseFragment
import com.kuemiin.animalfight.base.RecordingsAdapter
import com.kuemiin.animalfight.ui.dialog.delete.DeleteAudioDialog
import com.kuemiin.animalfight.utils.CommonUtils
import com.kuemiin.animalfight.utils.FileUtils
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.PermissionUtils
import com.kuemiin.animalfight.utils.extension.gone
import com.kuemiin.animalfight.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import cn.ezandroid.lib.ezfilter.extra.sticker.Bitmap2025QuizFilter
import com.kuemiin.animalfight.model.MyGalleryModel
import com.kuemiin.animalfight.ui.fragment.permission.PermisionFragment
import com.kuemiin.animalfight.ui.fragment.preview_gallery.PreviewGalleryFragment
import com.kuemiin.animalfight.ui.fragment.trythis.TryThisFragment
import com.kuemiin.animalfight.utils.MaxUtils.logFirebaseEvent

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    companion object {
        var isDownloadedRecent = false
    }

    private var wl: PowerManager.WakeLock? = null

    private val viewModel by viewModels<MainFragmentViewModel>()
    private var dialogDelete: DeleteAudioDialog? = null

    private val adapterRecording by lazy {
        RecordingsAdapter(R.layout.item_recording).apply {
            viewmodel = viewModel
            liveState = viewModel.appPlayer.liveState
            isPlayReverse = viewModel.appPlayer.isPlayReverse
            pathPlaying = viewModel.appPlayer.pathPlaying
        }
    }

    private val mAdapterVideo by lazy {
        GalleryAdapter(
            layoutInflater,
            R.layout.item_video_record,
            R.layout.item_video_record//layout native

        ).apply {
            viewmodel = viewModel
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_main

    override fun setUp() {
        hideKeyboard()
        viewModel.activity = requireActivity()
        binding.viewmodel = viewModel
        setUpAdapter()
        initEvent()
    }
    //endregion

    private fun setUpAdapter() {
//        binding.rcvRecording.adapter = adapterRecording
//
//        val linearLayoutManager = GridLayoutManager(requireContext(), 2)
//        binding.rcvItemsVideos.setHasFixedSize(true)
//        binding.rcvItemsVideos.adapter = mAdapterVideo
//        binding.rcvItemsVideos.layoutManager = linearLayoutManager
    }

    @SuppressLint("NotifyDataSetChanged", "InvalidWakeLockTag")
    private fun initEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainEvent.collect { event ->
                    when (event) {
                        //region tab home
                        MainFragmentViewModel.MainEvent.BackToHome -> {

                        }

                        MainFragmentViewModel.MainEvent.OnClickBottom -> {
                            when (viewModel.typeBottomSelected.get()) {
                                Constant.MAIN_TYPE_HOME -> {
                                }

                                Constant.MAIN_TYPE_RECORDINGS -> {
                                }

                                else -> {
                                }
                            }
                        }

                        MainFragmentViewModel.MainEvent.NavigateOnClickJungleWord -> {
                        }

                        MainFragmentViewModel.MainEvent.OnClickJungleWord -> {

                            if (PermissionUtils.checkHasPerRecord(requireActivity())) {
                                MaxUtils.showAdsWithCustomCount(
                                    requireActivity(),
                                    object : MaxUtils.NextScreenListener {
                                        override fun nextScreen() {
                                            viewModel.NavigateOnClickJungleWord()
                                        }
                                    })
                            } else {
                                navigateTo(
                                    R.id.action_mainFragment_to_permisionFragment,
                                    PermisionFragment.newBundle(false, "")
                                )
                            }
                        }

                        MainFragmentViewModel.MainEvent.OnClickDeathBattle -> {

                            if (PermissionUtils.checkHasPerCameraAndRecord(requireActivity())) {
                                MaxUtils.showAdsWithCustomCount(
                                    requireActivity(),
                                    object : MaxUtils.NextScreenListener {
                                        override fun nextScreen() {
                                            viewModel.NavigateChooseLevel()
                                        }
                                    })
                            } else {
                                navigateTo(
                                    R.id.action_mainFragment_to_permisionFragment,
                                    PermisionFragment.newBundle(
                                        true,
                                        Bitmap2025QuizFilter.EFFECT_SING_A_SONG
                                    )
                                )
                            }
                        }

                        MainFragmentViewModel.MainEvent.NavigateChooseLevel -> {
                            navigateTo(
                                R.id.action_mainFragment_to_tryThisFragment,
                                TryThisFragment.newBundle(Bitmap2025QuizFilter.EFFECT_SING_A_SONG)
                            )
                        }


                        MainFragmentViewModel.MainEvent.OnClickQuestionAnimal -> {
                            if (PermissionUtils.checkHasPerCameraAndRecord(requireActivity())) {
                                MaxUtils.showAdsWithCustomCount(
                                    requireActivity(),
                                    object : MaxUtils.NextScreenListener {
                                        override fun nextScreen() {
                                            viewModel.NavigateOnClickQuestionAnimal()
                                        }
                                    })
                            } else {
                                navigateTo(
                                    R.id.action_mainFragment_to_permisionFragment,
                                    PermisionFragment.newBundle(
                                        true,
                                        Bitmap2025QuizFilter.EFFECT_ANIMAL_EFFECT
                                    )
                                )
                            }
                        }

                        MainFragmentViewModel.MainEvent.NavigateOnClickQuestionAnimal -> {
                            navigateTo(
                                R.id.action_mainFragment_to_tryThisFragment,
                                TryThisFragment.newBundle(Bitmap2025QuizFilter.EFFECT_ANIMAL_EFFECT)
                            )
                        }

                        MainFragmentViewModel.MainEvent.OnClickStartRecord -> {

                            PermissionUtils.requestPermissionRecord(requireActivity(), true, {
                                viewModel.onClickStartRecordAfterCheckPermission()
                            }, {

                            })
                        }

                        MainFragmentViewModel.MainEvent.OnClickStopRecord -> {
                            if (viewModel.liveStateRecord.value != VisualizerRecorder.STATE_RECORD_IDLE) {
                                hasPausedRecord = true
                                viewModel.pauseRecord()
                                viewModel.releaseRecord()
                                viewModel.createNewReverseAudio()
                            } else {
                            }
                        }

                        MainFragmentViewModel.MainEvent.OnClickPlayRecorded -> {
                        }

                        MainFragmentViewModel.MainEvent.OnClickPlayReverse -> {
                        }

                        MainFragmentViewModel.MainEvent.ShowLoading -> {
//                            if (viewModel.isLoading.get()) {
//                                binding.clLoading.visible()
//                            } else {
//                                binding.clLoading.gone()
//                            }
                        }
                        //endregion tab home

                        //region tab recording
                        is MainFragmentViewModel.MainEvent.EndAudioTabRecord -> {

                        }

                        is MainFragmentViewModel.MainEvent.PauseAudioTabRecord -> {

                        }

                        is MainFragmentViewModel.MainEvent.NotifyAdapterRecording -> {
                            adapterRecording.submitCategories(viewModel.listAudio)
                        }

                        is MainFragmentViewModel.MainEvent.NavigateOnClickItemVideo -> {
                            navigateTo(
                                R.id.action_mainFragment_to_previewGalleryFragment,
                                PreviewGalleryFragment.newBundle(event.item)
                            )
                        }

                        is MainFragmentViewModel.MainEvent.OnClickItemVideo -> {
                            requireActivity().logFirebaseEvent("click_my_file_video_item")
                            MaxUtils.showAdsWithCustomCount(
                                requireActivity(),
                                object : MaxUtils.NextScreenListener {
                                    override fun nextScreen() {
                                        viewModel.NavigateOnClickItemGallery(event.item)
                                    }
                                })
                        }

                        is MainFragmentViewModel.MainEvent.OnHandleDelete -> {
                            if (event.list.isEmpty()) return@collect
                            dialogDelete?.dismiss()
                            dialogDelete?.cancel()
                            dialogDelete = null
                            dialogDelete = DeleteAudioDialog(requireContext())
                            dialogDelete?.setUpData {
                                FileUtils.deleteFileInternalAudio(event.list.first())
                                viewModel.getListAudio()
                            }
                            dialogDelete?.show()
                        }

                        is MainFragmentViewModel.MainEvent.OnHandleShare -> {

                            FileUtils.shareMultiplePath(
                                event.list.map { it.path },
                                requireActivity()
                            )
                        }
                        //endregion audio recording

                        //region tab settings
                        MainFragmentViewModel.MainEvent.OnClickShare -> {

                            CommonUtils.shareApp(requireActivity())
                        }

                        MainFragmentViewModel.MainEvent.OnClickPolicy -> {

                            var url =
                                "https://docs.google.com/document/d/1HZxtyU6XqHdzLIDLERUNuo7RGuVY5blPlfspf7OMft0/edit"
                            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                url = "http://$url"
                            }

                            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
                            startActivity(browserIntent)
                        }

                        MainFragmentViewModel.MainEvent.OnClickTermOfUse -> {

                        }

                        MainFragmentViewModel.MainEvent.NotifyAdapterVideo -> {
//                            binding.rcvItemsVideos.post {
//                                binding.clEmpty.visibility =
//                                    if (viewModel.isEmptyVideo.get() && !viewModel.isLoading.get()) View.VISIBLE else View.GONE
//                                val list = arrayListOf<MyGalleryModel>()
//                                viewModel.listCreation.forEach {
//                                    val demo = it.clone()
//                                    list.add(demo)
//                                }
//
//                                binding.rcvItemsVideos.post {
//                                    mAdapterVideo.setData(list)
//                                    if (!viewModel.isEmptyVideo.get()) binding.rcvItemsVideos.visible()
//                                    binding.rcvItemsVideos.invalidate()
//                                }
//                            }
                        }
                        //endregion tab settings
                    }.exhaustive
                }
            }
        }

        val pm = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag")
        wl?.acquire(10 * 60 * 1000L /*10 minutes*/)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    //region LIFECYCLER

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (viewModel.isRecording.get()) {
            return
        }

        if (viewModel.isSelectMore.get()) {
            viewModel.onClickCancelMore()
            return
        }

        if (doubleBackToExitPressedOnce) {
            requireActivity().finish()
            return
        }
        doubleBackToExitPressedOnce = true
        toast("Tap again to exit")
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        wl?.release()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private var hasPausedRecord = false
    override fun onPause() {
        super.onPause()
        if (!hasPausedRecord) viewModel.pauseRecord()
        viewModel.pauseAudio()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkListVideo()
    }

    //endregion
}