package com.kuemiin.animalfight.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lhd.visualizer_record.VisualizerRecorder
import com.lhd.visualizer_record.view.VisualizerView
import com.kuemiin.animalfight.base.BaseViewModel
import com.kuemiin.animalfight.data.repository.MainRepository
import com.kuemiin.animalfight.data.store.DataStoreHelper
import com.kuemiin.animalfight.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.kuemiin.animalfight.data.store.DataStoreHelper.Companion.KEY_LOOP_PLAYBACK
import com.kuemiin.animalfight.data.store.DataStoreHelper.Companion.KEY_PITCH_SHIFT
import com.kuemiin.animalfight.data.store.DataStoreHelper.Companion.KEY_PLAYBACK_SPEED
import com.kuemiin.animalfight.model.AppAudio
import com.kuemiin.animalfight.model.MyGalleryModel
import com.kuemiin.animalfight.utils.FileUtils
import com.kuemiin.animalfight.utils.FileUtils.REVERSE_AUDIO
import com.kuemiin.animalfight.utils.MaxUtils.logFirebaseEvent
import com.kuemiin.animalfight.utils.doJob
import com.kuemiin.animalfight.utils.extension.toast
import com.kuemiin.animalfight.utils.player.AppPlayer
import com.kuemiin.animalfight.utils.player.AudioHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MainFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val store: DataStoreHelper,
    val repository: MainRepository,
) : BaseViewModel(), VisualizerRecorder.Listener, AppPlayer.IAppPlayerListener {

    private val mainEventChannel = Channel<MainEvent>()
    val mainEvent = mainEventChannel.receiveAsFlow()
    var listAudio = arrayListOf<AppAudio>()
    var listAll = arrayListOf<AppAudio>()//use for filter, not use for adapter, query for category

    var listCreation = arrayListOf<MyGalleryModel>()
    var isEmptyVideo: ObservableBoolean = ObservableBoolean(false)


    var typeBottomSelected = ObservableInt(Constant.MAIN_TYPE_HOME)
    var isLoading = ObservableBoolean(false)
    var isSelectMore = ObservableBoolean(false)
    var isEmptyRecording = ObservableBoolean(false)
    var isSelectVideo = ObservableBoolean(false)

    var hasInitAudioAfterRecorded =
        ObservableBoolean(false)//sau khi record, sẽ có 2 file đc tạo ra -> false, hiện animation zoom, click play hoặc pause -> true, ẩn animation
    var isRecorded = ObservableBoolean(false)
    var isRecording = ObservableBoolean(false)

    var currentTempPath = ""
    var currentTempPathReverse = ""
    val liveCurrentTime by lazy { MutableLiveData("00:00") }
    private val timeFormat by lazy { DecimalFormat("00") }
    val liveStateRecord by lazy { visualizerRecorder.liveStateRecord }
    val visualizerRecorder = VisualizerRecorder().apply {
        listener = this@MainFragmentViewModel
        setChannelCount(AudioFormat.CHANNEL_IN_MONO)
        setSampleRate(44100)
    }

    private var mAudioHelper = AudioHelper {
        if (it) {
            pauseAudio()
        }
    }

    val appPlayer by lazy {
        AppPlayer().apply {
            listener = this@MainFragmentViewModel
            audioHelper = mAudioHelper
        }
    }


    var loopPlayback = ObservableBoolean(true)
    var playBackSpeed = ObservableFloat(1f)
    var pitchShift = ObservableFloat(1f)

    init {
        getListAudio()
        getSpeedAndPitchDB()
    }
    //endregion


    fun getListAudio() = viewModelScope.launch {
        doJob({
            FileUtils.getListDateGroupAudio(FileUtils.getFolderTemp().absolutePath)
        }, {
            listAll.clear()
            listAll.addAll(it.sortedByDescending { it.date })
            isEmptyRecording.set(listAll.isEmpty())
            filterList()
        }, dispathcherOut = Dispatchers.Main)
    }

    private fun filterList() = viewModelScope.launch {
        listAudio.clear()
        listAudio.addAll(listAll.filter { !it.path.contains("mp4") }.sortedByDescending { it.date })
        mainEventChannel.send(MainEvent.NotifyAdapterRecording)
    }

    // region override/ listener method
    fun setLoading(loading: Boolean) = viewModelScope.launch {
        isLoading.set(loading)
        mainEventChannel.send(MainEvent.ShowLoading)
    }

    //endregion

    fun onClickBottom(type: Int) = viewModelScope.launch {
        if (type != typeBottomSelected.get()) {
            viewModelScope.launch {
                pauseAudio()
                typeBottomSelected.set(type)
                mainEventChannel.send(MainEvent.OnClickBottom)
            }
        } else {
            pauseAudio()
            typeBottomSelected.set(type)
        }
    }


    fun onClickBackHome() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.BackToHome)
    }

    fun onClickJungleWord() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickJungleWord)
    }

    fun NavigateOnClickJungleWord() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateOnClickJungleWord)
    }

    fun onClickDeathBattle() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickDeathBattle)
    }

    fun NavigateChooseLevel() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateChooseLevel)
    }

    fun onClickQuestionAnimal() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickQuestionAnimal)
    }

    fun NavigateOnClickQuestionAnimal() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateOnClickQuestionAnimal)
    }

    fun onClickStartRecord() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickStartRecord)
    }

    fun onClickStartRecordAfterCheckPermission() = viewModelScope.launch {
        when (getStateRecord()) {
            VisualizerRecorder.STATE_PAUSE, VisualizerRecorder.STATE_RECORD_RUNNING -> {
                mainEventChannel.send(MainEvent.OnClickStopRecord)
            }

            else -> {
                startRecord()
            }
        }
    }

    fun onClickStopRecord() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickStopRecord)
    }

    fun onClickPlayRecorded() = viewModelScope.launch {
        if (typeBottomSelected.get() == Constant.MAIN_TYPE_HOME) {
            if (appPlayer.liveState.value == AppPlayer.State.PLAYING && appPlayer.isPlayReverse.value == false) {
                pauseAudio()
                mainEventChannel.send(MainEvent.PauseAudioTabRecord)

            } else {
                initAudio(false)
            }
            mainEventChannel.send(MainEvent.OnClickPlayRecorded)
        }

    }

    fun onClickPlayReverse() = viewModelScope.launch {
        if (typeBottomSelected.get() == Constant.MAIN_TYPE_HOME) {
            if (appPlayer.liveState.value == AppPlayer.State.PLAYING && appPlayer.isPlayReverse.value == true) {
                pauseAudio()
                mainEventChannel.send(MainEvent.PauseAudioTabRecord)

            } else {
                initAudio(true)
            }
            mainEventChannel.send(MainEvent.OnClickPlayReverse)
        }
    }


    //region TAB recording
    fun checkListVideo() = viewModelScope.launch {
        if (isLoading.get()) return@launch
        val all = repository.getAllVideosMyCreation().first().await() as List<MyGalleryModel>
        if ((listCreation.isNotEmpty() && listCreation.size != all.size) || listCreation.isEmpty()) {
            listCreation.clear()
            all.let {
                listCreation.addAll(it)
//                listCreation = CommonUtils.getListNative(listCreation, 3, createGalleryEmpty())
            }
            listCreation.forEach {
                it.duration = it.getAudioDuration(it.currentVideo)
            }
            isEmptyVideo.set(listCreation.isEmpty())
            isLoading.set(false)
            mainEventChannel.send(MainEvent.NotifyAdapterVideo)
        }
    }

    fun onClickCancelMore() = viewModelScope.launch {
        isSelectMore.set(false)
    }

    fun onClickTabVideo(select: Boolean) = viewModelScope.launch {
        isSelectVideo.set(select)
    }

    fun NavigateOnClickItemGallery(item: MyGalleryModel) = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateOnClickItemVideo(item))
    }

    fun onClickItemGallery(item: MyGalleryModel) = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickItemVideo(item))
    }

    fun onClickDeleteMore() = viewModelScope.launch {
        isSelectMore.set(false)
        val listDelete = listOf(itemMore!!)
        mainEventChannel.send(MainEvent.OnHandleDelete(listDelete))
    }

    fun onClickShareMore() = viewModelScope.launch {
        val listDelete = listOf(itemMore!!)
        mainEventChannel.send(MainEvent.OnHandleShare(listDelete))
    }

    fun onClickPlayNormalItemRecording(item: AppAudio) = viewModelScope.launch {
        context.logFirebaseEvent("click_my_file_recording_item")

        val itemPlay = listAudio.first { it.path == item.path }
        if (typeBottomSelected.get() == Constant.MAIN_TYPE_RECORDINGS) {
            if (appPlayer.liveState.value == AppPlayer.State.PLAYING && appPlayer.isPlayReverse.value == false) {
                pauseAudio()
                mainEventChannel.send(MainEvent.PauseAudioTabRecord)

            } else {
                itemPlay.isPlayingNormal = true
                initAudioRecording(false, itemPlay)
            }
            mainEventChannel.send(MainEvent.NotifyAdapterRecording)
        }
    }

    fun onClickPlayReverseItemRecording(item: AppAudio) = viewModelScope.launch {
        val itemPlay = listAudio.first { it.path == item.path }
        if (typeBottomSelected.get() == Constant.MAIN_TYPE_RECORDINGS) {
            if (appPlayer.liveState.value == AppPlayer.State.PLAYING && appPlayer.isPlayReverse.value == true) {
                pauseAudio()
                mainEventChannel.send(MainEvent.PauseAudioTabRecord)

            } else {
                itemPlay.isPlayingReverse = true
                initAudioRecording(true, itemPlay)
            }
            mainEventChannel.send(MainEvent.NotifyAdapterRecording)
        }
    }

    var itemMore: AppAudio? = null
    fun onClickMoreItemRecording(item: AppAudio) = viewModelScope.launch {
        itemMore = item
        pauseAudio()
        isSelectMore.set(true)
    }

    //endregion

    //region TAB setting

    fun getSpeedAndPitchDB() = viewModelScope.launch {
        loopPlayback.set(store.loopPlayback.first()).await()
        playBackSpeed.set(store.playBackSpeed.first()).await()
        pitchShift.set(store.pitchShift.first()).await()
    }

    fun onChangeSpeed(speed: Float) = viewModelScope.launch {//5-20
        store.setValueFloat(speed, KEY_PLAYBACK_SPEED)
    }

    fun onChangePitchShift(pitchShift: Float) = viewModelScope.launch {//5-20
        store.setValueFloat(pitchShift, KEY_PITCH_SHIFT)
    }

    fun onClickSwLoop() = viewModelScope.launch {
        store.setValueBoolean(!loopPlayback.get(), KEY_LOOP_PLAYBACK)
    }

    fun onClickShare() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickShare)
    }

    fun onClickPolicy() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickPolicy)
    }

    fun onClickTermOfUse() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.OnClickTermOfUse)
    }
    //endregion

    //region RECORD AUDIO
    fun attachToVisualizerView(visualizerView: VisualizerView?) {
        visualizerRecorder.attachToVisualizerView(visualizerView)
    }

    private fun Long.convertToStringTime(): String {
        val timeSecond = this / 1000
        val second = timeSecond % 60
        val h = timeSecond / (60 * 60)
        val minute = (timeSecond % (60 * 60)) / 60
        return "${timeFormat.format(minute + h * 60)}:${timeFormat.format(second)}"
    }

    private fun getNameDateFormat(): String {
        val current_data_time = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_a", Locale.getDefault())
        return current_data_time.format(Date())
    }

    private fun getRecordOutputPath(): String {
        var index = 1
        val folderPath = FileUtils.getFolderTemp()
        var pathOutput = "$folderPath/Audio_$index.${FileUtils.EXTENSION_RECORD}"
        while (File(pathOutput).exists()) {
            index++
            pathOutput = "$folderPath/Audio_$index.${FileUtils.EXTENSION_RECORD}"
        }
        return pathOutput
    }


    fun getRecordOutputName() = File(getRecordOutputPath()).nameWithoutExtension

    private fun getStateRecord() =
        visualizerRecorder.liveStateRecord.value ?: VisualizerRecorder.STATE_RECORD_IDLE

    fun startRecord() {
        val liveAudioName = getRecordOutputName()
        isRecording.set(true)
        isRecorded.set(false)
        hasInitAudioAfterRecorded.set(false)
        liveCurrentTime.postValue("00:00")
        currentTempPath =
            "${FileUtils.getFolderTemp()}/${liveAudioName ?: ""}.${FileUtils.EXTENSION_RECORD}"
        visualizerRecorder.startRecord(currentTempPath)
    }

    fun pauseRecord() {
        if (isRecording.get() || getStateRecord() == VisualizerRecorder.STATE_PAUSE || getStateRecord() == VisualizerRecorder.STATE_RECORD_RUNNING) {
            isRecording.set(false)
            isRecorded.set(true)
            hasInitAudioAfterRecorded.set(false)
            visualizerRecorder.pauseRecord()
        }
    }

    fun resumeRecord() {
        isRecording.set(true)
        visualizerRecorder.resumeRecord()
    }

    fun releaseRecord() {
        liveCurrentTime.postValue("00:00")
        visualizerRecorder.releaseRecord()
    }

    fun createNewReverseAudio() = viewModelScope.launch {
        setLoading(true)
        currentTempPathReverse =
            "${FileUtils.getFolderTempReverse()}/${File(currentTempPath).nameWithoutExtension + REVERSE_AUDIO}.${FileUtils.EXTENSION_RECORD}"
        FileUtils.reverseAudioBytesManual(currentTempPath, currentTempPathReverse).await()

        delay(1000)
        getListAudio()
        setLoading(false)
    }

    fun saveToRoomDB() = viewModelScope.launch {

    }

    override fun onRecordDurationChange(durationInMillis: Long) {
        liveCurrentTime.postValue(durationInMillis.convertToStringTime())
    }
    //endregion

    //region MEDIAPlayer
    fun initAudio(isReverse: Boolean) {
        pauseAudio()
        releaseRecord()
        appPlayer.init(if (isReverse) currentTempPathReverse else currentTempPath)
        hasInitAudioAfterRecorded.set(true)
    }

    fun initAudioRecording(isReverse: Boolean, item: AppAudio) {
        pauseAudio()
        releaseRecord()
        appPlayer.initItemTabRecording(isReverse, item)
        hasInitAudioAfterRecorded.set(true)
    }

    fun playAudio() {
        hasInitAudioAfterRecorded.set(true)
        appPlayer.play()
    }

    fun pauseAudio() {
        hasInitAudioAfterRecorded.set(true)
        appPlayer.pause()
    }

    override fun onLoadStart() {
    }

    override fun onLoadComplete() {
        isLoading.set(false)
        playAudio()
    }

    override fun onProgressChange(progress: Long) {
//        currentTime.set(progress.toInt())
//        progressPercent.set(progress.toFloat())
//        loge("onProgressChange PreviewModel ---  $progress")
    }

    override fun onPlayerError() {
        super.onPlayerError()
        isLoading.set(false)
        toast("This file is error!")
    }

    override fun onVideoEnd() {
        super.onVideoEnd()
        viewModelScope.launch {
//            if (typeBottomSelected.get() == Constant.MAIN_TYPE_RECORDINGS) {
            mainEventChannel.send(MainEvent.EndAudioTabRecord)
//            }
        }
    }

    override fun onChangeStatePlay() {
        viewModelScope.launch {
            if (typeBottomSelected.get() == Constant.MAIN_TYPE_RECORDINGS) {
                mainEventChannel.send(MainEvent.NotifyAdapterRecording)
            }
        }
    }
    //endregion

    sealed class MainEvent {
        //main
        object ShowLoading : MainEvent()
        object OnClickBottom : MainEvent()
        object OnClickJungleWord : MainEvent()
        object NavigateOnClickJungleWord : MainEvent()
        object OnClickDeathBattle : MainEvent()
        object NavigateChooseLevel : MainEvent()
        object OnClickQuestionAnimal : MainEvent()
        object NavigateOnClickQuestionAnimal : MainEvent()
        object BackToHome : MainEvent()

        //reverse
        object OnClickStartRecord : MainEvent()
        object OnClickStopRecord : MainEvent()
        object OnClickPlayRecorded : MainEvent()
        object OnClickPlayReverse : MainEvent()

        //recording
        object NotifyAdapterVideo : MainEvent()
        object NotifyAdapterRecording : MainEvent()
        object EndAudioTabRecord : MainEvent()
        object PauseAudioTabRecord : MainEvent()
        class NavigateOnClickItemVideo(val item: MyGalleryModel) : MainEvent()
        class OnClickItemVideo(val item: MyGalleryModel) : MainEvent()
        class OnHandleDelete(val list: List<AppAudio>) : MainEvent()
        class OnHandleShare(val list: List<AppAudio>) : MainEvent()

        //setting
        object OnClickShare : MainEvent()
        object OnClickPolicy : MainEvent()
        object OnClickTermOfUse : MainEvent()
    }
}
