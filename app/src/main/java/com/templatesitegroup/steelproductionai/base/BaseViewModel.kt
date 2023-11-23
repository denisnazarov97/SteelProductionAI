package com.templatesitegroup.steelproductionai.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<
    State : ScreenState,
    SupportedCommandType : Command>(initialScreenState: State) : ViewModel() {

    private var currentScreenState: State = initialScreenState
    protected var screenState: State
        get() = currentScreenState
        protected set(value) {
            updateScreenState(value)
        }

    private val screenStateMutableLiveData:
        MutableLiveData<State> = MutableLiveData()

    private val commandsMutableLiveData:
        SingleLiveEvent<SupportedCommandType> = SingleLiveEvent()

    val screenStateLiveData:
        LiveData<State> = screenStateMutableLiveData

    val commandsLiveData:
        LiveData<SupportedCommandType> = commandsMutableLiveData


    @Synchronized
    protected fun updateScreenState(screenState: State) {
        currentScreenState = screenState
        refreshView()
    }

    private fun refreshView() {
        screenStateMutableLiveData.postValue(screenState)
    }

    protected fun executeCommand(command: SupportedCommandType) {
        commandsMutableLiveData.postValue(command)
    }
}
