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

/**
 * Use case for the desktop domain.
 */
class DesktopUseCase(private val accessUseCase: AccessUseCase, private val repository: DesktopDomainRepository) {

    /**
     * Download the desktop.
     *
     * @return the result of the download with the desktop.
     */
    suspend fun downloadDesktop(): Result<Desktop> =
        accessUseCase.getLoggedUser().mapCatching { user -> repository.downloadDesktop(user) }

    /**
     * Gets the desktop.
     *
     * @return the result of the get with the desktop.
     */
    suspend fun getDesktop(): Result<Desktop> =
        accessUseCase.getLoggedUser().mapCatching { user -> repository.getDesktop(user) }

    /**
     * Adds a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @return the result of the add with the new desktop.
     */
    suspend fun addSchoolSupply(schoolSupply: SchoolSupply): Result<Desktop> =
        accessUseCase.getLoggedUser().mapCatching { user ->
            val desktop = repository.getDesktop(user)
            repository.addSchoolSupply(user, schoolSupply)
            desktop.addSchoolSupply(schoolSupply)
            desktop
        }

    /**
     * Get the book given the isbn.
     *
     * @param isbn The isbn.
     * @return the result of the get with the book.
     */
    suspend fun getBook(isbn: String): Result<Book?> = kotlin.runCatching { repository.getBook(isbn) }

    /**
     * Gets the school supply given the rfid.
     *
     * @param rfid The rfid.
     * @return the result of the get with the school supply.
     */
    suspend fun getSchoolSupply(rfid: String): Result<SchoolSupply?> =
        accessUseCase.getLoggedUser().mapCatching { user ->
            repository.getDesktop(user).schoolSupplies.firstOrNull { it.rfidCode == rfid }
        }

    /**
     * associate the backpack.
     *
     * @param hash The hash of the backpack.
     * @return the result of the associate with the new desktop.
     */
    suspend fun associateBackpack(hash: String): Result<Desktop> =
        accessUseCase.getLoggedUser().mapCatching { user ->
            val returnedHash = repository.associateBackpack(user, hash)
            val desktop = repository.getDesktop(user)
            desktop.associateBackpack(returnedHash)
            desktop
        }

    /**
     * Subscribes to the backpack.
     * The states of the backpack are a [Flow] of [Set] of [SchoolSupply].
     * The [Flow] emits the [Set] of [SchoolSupply] when the backpack changes.
     * The repository emits the changes of the backpack when the desktop changes.
     * The desktop changes when the backpack changes.
     *
     * @return the result of the subscribe with the [Flow] of [Set] of [SchoolSupply].
     */
    suspend fun subscribeToBackpack(): Result<Flow<Set<SchoolSupply>>> =
        accessUseCase.getLoggedUser().mapCatching { user ->
            repository.subscribeToBackpack(user)
                .map {
                    repository.getDesktop(user).schoolSupplies.filter { supply ->
                        it.contains(supply.rfidCode)
                    }.toSet()
                }
                .conflate()
        }

    /**
     * Delete the desktop.
     *
     * @return the result of the logout.
     */
    suspend fun logoutDesktop(): Result<Unit> =
        accessUseCase.getLoggedUser().mapCatching { user -> repository.logoutDesktop(user) }

    /**
     * Disassociate a backpack from the desktop.
     *
     * @param hash The hash of the backpack.
     * @return the result of the disassociate with the new desktop.
     */
    suspend fun disassociateBackpack(hash: String): Result<Desktop> =
        accessUseCase.getLoggedUser().let { result ->
            if (result.isFailure) {
                Result.failure(result.exceptionOrNull()!!)
            } else {
                val user = result.getOrNull()!!
                val desktop = repository.getDesktop(user)
                if (desktop.isBackpackAssociated) {
                    if (desktop.backpack == hash) {
                        try {
                            repository.disassociateBackpack(user, hash)
                            desktop.disassociateBackpack(hash)
                            Result.success(desktop)
                        } catch (e: Exception) {
                            Result.failure(e)
                        }
                    } else {
                        Result.failure(BackpackNotAssociatedException())
                    }
                } else {
                    Result.failure(BackpackNotAssociatedException())
                }
            }
        }

    suspend fun deleteDesktop() {
        accessUseCase.getLoggedUser().mapCatching { user -> repository.deleteDesktop(user) }
    }
}
