package com.kuemiin.animalfight.ui.fragment.language

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.viewModelScope
import com.kuemiin.animalfight.base.BaseViewModel
import com.kuemiin.animalfight.data.repository.LanguageRepository
import com.kuemiin.animalfight.data.store.DataStoreHelper
import com.kuemiin.animalfight.model.Language
import com.kuemiin.animalfight.utils.player.AppPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class LanguageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val repo : DataStoreHelper,
    val languageRepo: LanguageRepository
) : BaseViewModel(), AppPlayer.IAppPlayerListener {

    private val languageEventChannel = Channel<Event>()
    val homeEvent = languageEventChannel.receiveAsFlow()
    var isLoading : ObservableBoolean = ObservableBoolean(true)

    val listLanguage = languageRepo.getListLanguageSingAlong()
    //endregion

    fun onClickBack() = viewModelScope.launch {
        languageEventChannel.send(Event.OnClickBack)
    }

    fun OnNavigateUp() = viewModelScope.launch {
        languageEventChannel.send(Event.OnNavigateUp)
    }

    fun onClickLetStart() = viewModelScope.launch {
        languageEventChannel.send(Event.OnClickLetStart)
    }

    fun NavigateOnClickTryThis() = viewModelScope.launch {
        languageEventChannel.send(Event.NavigateOnClickLetStart)
    }

    fun onClickItemLanguage(item : Language) = viewModelScope.launch {
        listLanguage.forEach {
            it.isSelected = item.title == it.title
        }
        languageEventChannel.send(Event.OnClickItemLanguage(item))

    }

    sealed class Event {
        object OnNavigateUp : Event()
        object OnClickBack : Event()
        object OnClickLetStart : Event()
        object NavigateOnClickLetStart : Event()

        class OnClickItemLanguage(val item : Language) : Event()
    }

}