package com.kuemiin.animalfight.ui.fragment.processing

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import com.kuemiin.animalfight.data.repository.MainRepository
import com.kuemiin.animalfight.base.BaseViewModel
import com.kuemiin.animalfight.model.EncoderModel
import com.kuemiin.animalfight.utils.player.AppPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
@ExperimentalCoroutinesApi
class ProcessingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: MainRepository
) : BaseViewModel(), AppPlayer.IAppPlayerListener {

    private val previewDIYEventChannel = Channel<Event>()
    val homeEvent = previewDIYEventChannel.receiveAsFlow()
    var isLoading : ObservableBoolean = ObservableBoolean(true)
    var percent : ObservableInt = ObservableInt(0)
    var isDone = false
    var encoderModel : EncoderModel = EncoderModel()
    var pathVideo : String = ""

    //endregion


    private val countdownDurationMillis = TimeUnit.SECONDS.toMillis(Random.nextLong(12, 15))
    private val updateIntervalMillis = 100L
    private val _remainingTime = MutableStateFlow(countdownDurationMillis)
    val remainingTime = _remainingTime.asStateFlow()
    init {
        start()
    }

    private var countdownJob: Job? = null

    /**
     * Starts the 7-second countdown.
     * If a countdown is already running, it will be cancelled and restarted.
     */
    fun start() {
        // Cancel any existing countdown to avoid multiple timers running.
        countdownJob?.cancel()

        // Launch a new coroutine for the countdown logic.
        countdownJob = viewModelScope.launch(Dispatchers.Default) {
            var currentTime = countdownDurationMillis
            _remainingTime.value = currentTime

            // Loop until the countdown is finished.
            while (currentTime > 0) {
                // Wait for the specified interval.
                delay(updateIntervalMillis)
                // Decrement the time and emit the new value.
                currentTime -= updateIntervalMillis
                if(currentTime <= 0){
                    isDone = true
                    previewDIYEventChannel.send(Event.NavigateResult)
                }
                percent.set(100 - (currentTime.toFloat() / countdownDurationMillis.toFloat() * 100).toInt())
                _remainingTime.value = if (currentTime < 0) 0 else currentTime
            }

            // Optional: You could add a callback or emit a special event here
            // to signal that the countdown has finished.
        }
    }

    /**
     * Stops and cancels the currently running countdown.
     * The remaining time will not be reset.
     */
    fun stop() {
        countdownJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }


    fun onClickBack() = viewModelScope.launch {
        previewDIYEventChannel.send(Event.OnClickBack)
    }

    fun NavigateResult() = viewModelScope.launch {
        previewDIYEventChannel.send(Event.NavigateResult)
    }
    fun NavigateHome() = viewModelScope.launch {
        previewDIYEventChannel.send(Event.NavigateHome)
    }

    fun deleteVideo(){
        if(!isDone) File(pathVideo).delete()
    }


    sealed class Event {
        object OnClickBack : Event()
        object GoToHome : Event()
        object NavigateHome : Event()
        object NavigateResult : Event()
    }

}