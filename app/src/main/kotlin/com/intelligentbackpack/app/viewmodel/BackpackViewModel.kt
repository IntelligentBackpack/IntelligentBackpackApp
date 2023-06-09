package com.intelligentbackpack.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intelligentbackpack.app.App
import com.intelligentbackpack.app.exceptionhandler.ExceptionMessage.messageOrDefault
import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.SchoolSupplyAdapter.fromDomainToView
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * View model for the backpack.
 */
class BackpackViewModel(
    private val desktopUseCase: DesktopUseCase,
) : ViewModel() {

    private val backpackImpl = MutableLiveData<Set<SchoolSupplyView>>()

    /**
     * Live data with representing the backpack.
     */
    val backpack: LiveData<Set<SchoolSupplyView>> = backpackImpl

    private val isBackpackAssociatedImpl = MutableLiveData<Boolean>()

    /**
     * Live data with representing if the backpack is associated.
     */
    val isBackpackAssociated: LiveData<Boolean> = isBackpackAssociatedImpl

    /**
     * updates the livedata isBackpackAssociated.
     *
     * @param error the error callback.
     */
    fun getBackpackAssociated(
        error: (error: String) -> Unit,
    ) {
        viewModelScope.launch {
            desktopUseCase.getDesktop()
                .onSuccess {
                    isBackpackAssociatedImpl.postValue(it.isBackpackAssociated)
                }.onFailure {
                    error(it.messageOrDefault())
                }
        }
    }

    /**
     * Associates a new backpack.
     *
     * @param hash the hash of the backpack.
     * @param success the success callback.
     * @param error the error callback.
     */
    fun associateBackpack(
        hash: String,
        success: () -> Unit,
        error: (error: String) -> Unit,
    ) {
        viewModelScope.launch {
            desktopUseCase.associateBackpack(hash)
                .onSuccess {
                    isBackpackAssociatedImpl.postValue(true)
                    success()
                }.onFailure {
                    error(it.messageOrDefault())
                }
        }
    }

    /**
     * Disassociates a backpack.
     *
     * @param success the success callback.
     * @param error the error callback.
     */
    fun disassociateBackpack(
        success: () -> Unit,
        error: (error: String) -> Unit,
    ) {
        viewModelScope.launch {
            desktopUseCase.getDesktop()
                .onSuccess { desktop ->
                    desktop.backpack?.let { backpack ->
                        viewModelScope.launch {
                            desktopUseCase.disassociateBackpack(backpack)
                                .onSuccess {
                                    isBackpackAssociatedImpl.postValue(false)
                                    success()
                                }
                                .onFailure {
                                    error(it.messageOrDefault())
                                }
                        }
                    }
                }.onFailure {
                    error(it.messageOrDefault())
                }
        }
    }

    /**
     * Subscribes to the backpack updates.
     *
     * @param error the error callback.
     */
    fun subscribe(
        error: (error: String) -> Unit,
    ) {
        viewModelScope.launch {
            desktopUseCase.subscribeToBackpack()
                .onSuccess {
                    viewModelScope.launch(Dispatchers.IO) {
                        it.map { backpack ->
                            backpack.map { schoolSupply -> schoolSupply.fromDomainToView() }.toSet()
                        }.collect {
                            viewModelScope.launch(Dispatchers.Main) {
                                backpackImpl.postValue(it)
                            }
                        }
                    }
                }.onFailure {
                    error(it.messageOrDefault())
                }
        }
    }

    companion object {

        /**
         * Factory for the view model.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the Application object from extras
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                BackpackViewModel(
                    (application as App).desktopUseCase,
                )
            }
        }
    }
}
