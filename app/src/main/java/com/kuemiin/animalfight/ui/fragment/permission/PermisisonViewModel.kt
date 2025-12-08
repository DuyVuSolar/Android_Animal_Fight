package com.kuemiin.animalfight.ui.fragment.permission

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.kuemiin.animalfight.BaseApplication
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseViewModel
import com.kuemiin.animalfight.data.store.DataStoreHelper
import com.kuemiin.animalfight.data.store.DataStoreHelper.Companion.KEY_HAS_SHOW_INTRO
import com.kuemiin.animalfight.model.IntroSplash
import com.kuemiin.animalfight.utils.MaxUtils
import com.kuemiin.animalfight.utils.MaxUtils.getBoolean
import com.kuemiin.animalfight.utils.isAndroid13
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class PermisisonViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val store: DataStoreHelper
) : BaseViewModel() {

    // region Const and Fields
    private val introEventChannel = Channel<IntroEvent>()
    val introEvent = introEventChannel.receiveAsFlow()

    var effect = ""
    val requestCamera = ObservableBoolean(false)


    var hasAllowCamera = ObservableBoolean(false)
    var hasAllowRecord = ObservableBoolean(false)

    fun onClickContinue() = viewModelScope.launch {
        introEventChannel.send(IntroEvent.OnClickContinue)
    }

    fun onClickContinueTryThis() = viewModelScope.launch {
        introEventChannel.send(IntroEvent.OnClickContinueTryThis)
    }

    fun onClickContinueReverse() = viewModelScope.launch {
        introEventChannel.send(IntroEvent.OnClickContinueReverse)
    }

    fun onClickPerMicro() = viewModelScope.launch {
        if(hasAllowRecord.get()) return@launch
        introEventChannel.send(IntroEvent.OnClickPerMicro)
    }

    fun onClickPerCamera() = viewModelScope.launch {
        if(hasAllowCamera.get()) return@launch
        introEventChannel.send(IntroEvent.OnClickPerCamera)
    }

    //region onclick

    // endregion

    sealed class IntroEvent {
        object OnClickContinue : IntroEvent()
        object OnClickContinueTryThis : IntroEvent()
        object OnClickContinueReverse : IntroEvent()
        object OnClickPerMicro : IntroEvent()
        object OnClickPerCamera : IntroEvent()
        object OnClickBackIntro : IntroEvent()
    }
}