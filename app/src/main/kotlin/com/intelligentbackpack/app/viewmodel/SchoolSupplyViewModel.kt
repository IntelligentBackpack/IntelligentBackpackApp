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
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.BookAdapter.fromDomainToView
import com.intelligentbackpack.app.viewdata.adapter.SchoolSupplyAdapter.fromDomainToView
import com.intelligentbackpack.app.viewdata.adapter.SchoolSupplyAdapter.fromViewToDomain
import com.intelligentbackpack.desktopdomain.policies.ISBNPolicy
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import kotlinx.coroutines.launch

/**
 * View model for the school supplies.
 */
class SchoolSupplyViewModel(
    private val desktopUseCase: DesktopUseCase,
) : ViewModel() {

    /**
     * Live data with the school supplies.
     */
    val schoolSupplies: LiveData<Set<SchoolSupplyView>>
        get() = schoolSuppliesImpl

    private val schoolSuppliesImpl = MutableLiveData<Set<SchoolSupplyView>>()

    /**
     * Live data with the school supply.
     */
    val schoolSupply: LiveData<SchoolSupplyView?>
        get() = schoolSupplyImpl

    private val schoolSupplyImpl = MutableLiveData<SchoolSupplyView?>()

    /**
     * Gets the school supplies.
     * The result is stored in the live data schoolSupplies.
     *
     * @param error the error callback.
     */
    fun getSchoolSupplies(
        error: (error: String) -> Unit,
    ) {
        viewModelScope.launch {
            desktopUseCase.getDesktop()
                .onSuccess { desktop ->
                    schoolSuppliesImpl.postValue(desktop.schoolSupplies.map { it.fromDomainToView() }.toSet())
                }.onFailure {
                    error(it.messageOrDefault())
                }
        }
    }

    /**
     * Gets the school supply.
     *
     * @param rfid the rfid of the school supply.
     * @param error the error callback.
     */
    fun getSchoolSupply(
        rfid: String,
        error: (error: String) -> Unit,
    ) {
        schoolSupplies.value?.firstOrNull { it.rfidCode == rfid }?.let {
            schoolSupplyImpl.postValue(it)
        } ?: viewModelScope.launch {
            desktopUseCase.getSchoolSupply(
                rfid,
            ).onSuccess {
                schoolSupplyImpl.postValue(it?.fromDomainToView())
            }.onFailure {
                error(it.messageOrDefault())
            }
        }
    }

    /**
     * Gets the book.
     *
     * @param isbn the isbn of the book.
     * @param success the success callback.
     * @param error the error callback.
     */
    fun getBook(
        isbn: String,
        success: (book: BookView) -> Unit,
        error: (error: String) -> Unit,
    ) {
        if (ISBNPolicy.isValid(isbn)) {
            viewModelScope.launch {
                desktopUseCase.getBook(isbn).onSuccess { book ->
                    book?.let {
                        success(it.fromDomainToView())
                    } ?: error("Book not found")
                }.onFailure {
                    error(it.messageOrDefault())
                }
            }
        } else {
            error("Invalid ISBN")
        }
    }

    /**
     * Creates a school supply.
     *
     * @param schoolSupplyView the school supply view.
     * @param success the success callback.
     * @param error the error callback.
     */
    fun createSchoolSupply(schoolSupplyView: SchoolSupplyView, success: () -> Unit, error: (String) -> Unit) {
        viewModelScope.launch {
            desktopUseCase.addSchoolSupply(schoolSupplyView.fromViewToDomain())
                .onSuccess {
                    schoolSuppliesImpl.postValue(it.schoolSupplies.map { it.fromDomainToView() }.toSet())
                    success()
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
                SchoolSupplyViewModel(
                    (application as App).desktopUseCase,
                )
            }
        }
    }
}
