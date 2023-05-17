package com.intelligentbackpack.desktopdomain.usecase

import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.exception.BackpackNotAssociatedException
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Use case for the desktop domain.
 */
class DesktopUseCase(private val accessUseCase: AccessUseCase, private val repository: DesktopDomainRepository) {

    suspend fun downloadDesktop(success: (Desktop) -> Unit, error: (Exception) -> Unit) {
        accessUseCase.automaticLogin({ user ->
            try {
                success(repository.downloadDesktop(user))
            } catch (e: Exception) {
                error(e)
            }
        }, error)
    }

    /**
     * Gets the desktop.
     *
     * @param success the success callback with the desktop.
     * @param error the error callback.
     */
    suspend fun getDesktop(success: (Desktop) -> Unit, error: (Exception) -> Unit) {
        accessUseCase.automaticLogin({ user ->
            try {
                success(repository.getDesktop(user))
            } catch (e: Exception) {
                error(e)
            }
        }, error)
    }

    /**
     * Adds a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @param success The success callback with all the school supplies.
     * @param error The error callback.
     */
    suspend fun addSchoolSupply(
        schoolSupply: SchoolSupply,
        success: (Desktop) -> Unit,
        error: (Exception) -> Unit,
    ) {
        accessUseCase.automaticLogin({ user ->
            runBlocking {
                try {
                    val desktop = repository.getDesktop(user)
                    desktop.addSchoolSupply(schoolSupply)
                    repository.addSchoolSupply(user, schoolSupply)
                    success(desktop)
                } catch (e: Exception) {
                    error(e)
                }
            }
        }, error)
    }

    /**
     * Get the book given the isbn.
     *
     * @param isbn The isbn.
     * @param success The success callback with the book.
     * @param error The error callback.
     */
    suspend fun getBook(isbn: String, success: (Book?) -> Unit, error: (Exception) -> Unit) {
        try {
            success(repository.getBook(isbn))
        } catch (e: Exception) {
            error(e)
        }
    }

    /**
     * Gets the school supply given the rfid.
     *
     * @param rfid The rfid.
     * @param success The success callback with the school supply.
     * @param error The error callback.
     */
    suspend fun getSchoolSupply(rfid: String, success: (SchoolSupply?) -> Unit, error: (Exception) -> Unit) {
        accessUseCase.automaticLogin({ user ->
            runBlocking {
                try {
                    success(repository.getDesktop(user).schoolSupplies.firstOrNull { it.rfidCode == rfid })
                } catch (e: Exception) {
                    error(e)
                }
            }
        }, error)
    }

    /**
     * associate the backpack.
     *
     * @param hash The hash of the backpack.
     * @param success The success callback with the desktop.
     * @param error The error callback.
     */
    suspend fun associateBackpack(hash: String, success: (Desktop) -> Unit, error: (Exception) -> Unit) {
        accessUseCase.automaticLogin({ user ->
            try {
                val returnedHash = repository.associateBackpack(user, hash)
                val desktop = repository.getDesktop(user)
                desktop.associateBackpack(returnedHash)
                success(desktop)
            } catch (e: Exception) {
                error(e)
            }
        }, error)
    }

    /**
     * Subscribes to the backpack.
     * The states of the backpack are a [Flow] of [Set] of [SchoolSupply].
     * The [Flow] emits the [Set] of [SchoolSupply] when the backpack changes.
     * The repository emits the changes of the backpack when the desktop changes.
     * The desktop changes when the backpack changes.
     *
     * @param success The success callback with a [Flow] of the school supplies in backpack.
     * @param error The error callback.
     */
    suspend fun subscribeToBackpack(success: (Flow<Set<SchoolSupply>>) -> Unit, error: (Exception) -> Unit) {
        accessUseCase.automaticLogin({ user ->
            try {
                val flow = repository.subscribeToBackpack(user)
                    .map {
                        repository.getDesktop(user).schoolSupplies.filter { supply ->
                            it.contains(supply.rfidCode)
                        }.toSet()
                    }
                    .conflate()
                success(flow)
            } catch (e: Exception) {
                error(e)
            }
        }, error)
    }

    /**
     * Delete the desktop.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    suspend fun logoutDesktop(success: () -> Unit, error: (Exception) -> Unit) {
        accessUseCase.automaticLogin({ user ->
            try {
                repository.logoutDesktop(user)
                success()
            } catch (e: Exception) {
                error(e)
            }
        }, error)
    }

    /**
     * Disassociate a backpack from the desktop.
     *
     * @param hash The hash of the backpack.
     * @param success The success callback.
     * @param error The error callback.
     */
    suspend fun disassociateBackpack(hash: String, success: (Desktop) -> Unit, error: (Exception) -> Unit) {
        accessUseCase.automaticLogin({ user ->
            val desktop = repository.getDesktop(user)
            if (desktop.isBackpackAssociated) {
                if (desktop.backpack == hash) {
                    try {
                        repository.disassociateBackpack(user, hash)
                        desktop.disassociateBackpack(hash)
                        success(desktop)
                    } catch (e: Exception) {
                        error(e)
                    }
                } else {
                    error(BackpackNotAssociatedException())
                }
            } else {
                error(BackpackNotAssociatedException())
            }
        }, error)
    }
}
