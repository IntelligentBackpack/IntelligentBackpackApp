package com.intelligentbackpack.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intelligentbackpack.app.App
import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.SchoolSupplyAdapter.fromDomainToView
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * View model for the backpack.
 */
class BackpackViewModel(
    private val desktopUseCase: DesktopUseCase
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
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            desktopUseCase.getDesktop({
                viewModelScope.launch(Dispatchers.Main) {
                    isBackpackAssociatedImpl.postValue(it.isBackpackAssociated)
                }
            }, {
                viewModelScope.launch(Dispatchers.Main) {
                    error(it.message ?: "Unknown error")
                }
            })
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
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            desktopUseCase.associateBackpack(hash, {
                viewModelScope.launch(Dispatchers.Main) {
                    isBackpackAssociatedImpl.postValue(true)
                    success()
                }
            }, {
                viewModelScope.launch(Dispatchers.Main) {
                    error(it.message ?: "Unknown error")
                }
            })
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
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            desktopUseCase.getDesktop({ desktop ->
                desktop.backpack?.let { backpack ->
                    viewModelScope.launch(Dispatchers.IO) {
                        desktopUseCase.disassociateBackpack(backpack, {
                            viewModelScope.launch(Dispatchers.Main) {
                                isBackpackAssociatedImpl.postValue(false)
                                success()
                            }
                        }, {
                            viewModelScope.launch(Dispatchers.Main) {
                                error(it.message ?: "Unknown error")
                            }
                        })
                    }
                }
            }, {
                viewModelScope.launch(Dispatchers.Main) {
                    error(it.message ?: "Unknown error")
                }
            })
        }
    }

    /**
     * Subscribes to the backpack updates.
     *
     * @param error the error callback.
     */
    fun subscribe(
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            /*(1..10).asFlow().map { (0..it).toSet() }.flowOn(Dispatchers.IO).collect {
                viewModelScope.launch(Dispatchers.Main) {
                    backpackImpl.postValue(it)
                }
            }*/
            desktopUseCase.subscribeToBackpack({
                viewModelScope.launch(Dispatchers.IO) {
                    it.map { backpack ->
                        backpack.map { schoolSupply -> schoolSupply.fromDomainToView() }.toSet()
                    }.flowOn(Dispatchers.IO).collect {
                        viewModelScope.launch(Dispatchers.Main) {
                            backpackImpl.postValue(it)
                        }
                    }
                }
            }, {
                viewModelScope.launch(Dispatchers.Main) {
                    error(it.message ?: "Unknown error")
                }
            })
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
                    (application as App).desktopUseCase
                )
            }
        }
    }
}