package com.intelligentbackpack.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.intelligentbackpack.app.App
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewdata.SchoolSupplyView
import com.intelligentbackpack.app.viewdata.adapter.BookAdapter.fromDomainToView
import com.intelligentbackpack.app.viewdata.adapter.SchoolSupplyAdapter.fromDomainToView
import com.intelligentbackpack.desktopdomain.policies.ISBNPolicy
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SchoolSupplyViewModel(
    private val desktopUseCase: DesktopUseCase
) : ViewModel() {

    val schoolSupplies: LiveData<Set<SchoolSupplyView>>
        get() = schoolSuppliesImpl

    private val schoolSuppliesImpl = MutableLiveData<Set<SchoolSupplyView>>()

    val schoolSupply: LiveData<SchoolSupplyView?>
        get() = schoolSupplyImpl

    private val schoolSupplyImpl = MutableLiveData<SchoolSupplyView?>()

    fun getSchoolSupplies(
        error: (error: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            desktopUseCase.getDesktop(
                { desktop ->
                    viewModelScope.launch(Dispatchers.Main) {
                        schoolSuppliesImpl.postValue(desktop.schoolSupplies.map { it.fromDomainToView() }.toSet())
                    }
                }
            ) {
                viewModelScope.launch(Dispatchers.Main) {
                    error(it.message ?: "Unknown error")
                }
            }
        }
    }

    fun getSchoolSupply(
        rfid: String,
        error: (error: String) -> Unit
    ) {
        schoolSupplies.value?.firstOrNull { it.rfidCode == rfid }?.let {
            schoolSupplyImpl.postValue(it)
        } ?: viewModelScope.launch(Dispatchers.IO) {
            desktopUseCase.getSchoolSupply(
                rfid,
                {
                    viewModelScope.launch(Dispatchers.Main) {
                        schoolSupplyImpl.postValue(it?.fromDomainToView())
                    }
                }
            ) {
                viewModelScope.launch(Dispatchers.Main) {
                    error(it.message ?: "Unknown error")
                }
            }
        }
    }

    fun getBook(
        isbn: String,
        success: (book: BookView) -> Unit,
        error: (error: String) -> Unit
    ) {
        if (ISBNPolicy.isValid(isbn))
            viewModelScope.launch(Dispatchers.IO) {
                desktopUseCase.getBook(isbn, { book ->
                    viewModelScope.launch(Dispatchers.Main) {
                        book?.let {
                            success(it.fromDomainToView())
                        } ?: error("Book not found")
                    }
                }, {
                    viewModelScope.launch(Dispatchers.Main) {
                        error(it.message ?: "Unknown error")
                    }
                })
                /*
            success(
                Book.build {
                    this.isbn = isbn
                    this.title = "The Art of Computer Programming"
                    this.authors = setOf("Donald Knuth")
                }.fromDomainToView()
            )*/
            }
        else
            error("Invalid ISBN")
    }

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the Application object from extras
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                SchoolSupplyViewModel(
                    (application as App).desktopUseCase
                )
            }
        }
    }
}
