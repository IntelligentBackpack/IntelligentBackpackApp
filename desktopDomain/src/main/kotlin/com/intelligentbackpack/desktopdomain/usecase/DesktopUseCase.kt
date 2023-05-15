package com.intelligentbackpack.desktopdomain.usecase

import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.BookCopy
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.SchoolSupplyTypes
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Use case for the desktop domain.
 */
class DesktopUseCase(private val repository: DesktopDomainRepository) {

    private var desktop: Desktop? = null

    /**
     * Gets the desktop.
     *
     * @param success the success callback with the desktop.
     * @param error the error callback.
     */
    suspend fun getDesktop(success: (Desktop) -> Unit, error: (Exception) -> Unit) {
        desktop?.let {
            success(it)
        } ?: repository.getDesktop(
            {
                desktop = it
                success(it)
            }, error
        )
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
        error: (Exception) -> Unit
    ) {
        val addSupply: (Desktop) -> Unit = {
            try {
                it.addSchoolSupply(schoolSupply)
                runBlocking {
                    repository.addSchoolSupply(schoolSupply, {
                        success(desktop!!)
                    }, error)
                }
            } catch (e: Exception) {
                error(e)
            }
        }
        getDesktop(addSupply, error)
    }

    suspend fun getBook(isbn: String, success: (Book?) -> Unit, error: (Exception) -> Unit) {
        val getBook: (Desktop) -> Unit = {
            try {
                val book = it.schoolSupplies
                    .filter { supply -> supply.type == SchoolSupplyTypes.BOOK }
                    .map { supply -> (supply as BookCopy).book }
                    .firstOrNull { book -> book.isbn == isbn }
                book?.let(success) ?: runBlocking { repository.getBook(isbn, success, error) }
            } catch (e: Exception) {
                error(e)
            }
        }
        getDesktop(getBook, error)
    }

    /**
     * Gets the school supply given the rfid.
     *
     * @param rfid The rfid.
     * @param success The success callback with the school supply.
     * @param error The error callback.
     */
    suspend fun getSchoolSupply(rfid: String, success: (SchoolSupply?) -> Unit, error: (Exception) -> Unit) {
        val getSchoolSupply: (Desktop) -> Unit = {
            try {
                success(it.schoolSupplies.firstOrNull { supply -> supply.rfidCode == rfid })
            } catch (e: Exception) {
                error(e)
            }
        }
        getDesktop(getSchoolSupply, error)
    }

    /**
     * Subscribes to the backpack.
     * The states of the backpack are a [Flow] of [Set] of [SchoolSupply].
     * The [Flow] emits the [Set] of [SchoolSupply] when the backpack changes.
     * The repository emits the changes of the backpack when the desktop changes.
     * The desktop changes when the backpack changes.
     * The repository receives the changes of the backpack with method [DesktopDomainRepository.putSchoolSuppliesInBackpack]
     * and [DesktopDomainRepository.takeSchoolSuppliesFromBackpack].
     *
     * @param success The success callback with a [Flow] of the school supplies in backpack.
     * @param error The error callback.
     */
    suspend fun subscribeToBackpack(success: (Flow<Set<SchoolSupply>>) -> Unit, error: (Exception) -> Unit) {

        val putSchoolSuppliesInBackpack = { backpack: Set<SchoolSupply> ->
            val newAdd = backpack - desktop?.schoolSuppliesInBackpack.orEmpty()
            desktop?.putSchoolSuppliesInBackpack(newAdd)
                ?.also {
                    runBlocking {
                        repository.putSchoolSuppliesInBackpack(
                            newAdd.map { supply -> supply.rfidCode }.toSet()
                        )
                    }
                }
        }

        val takeSchoolSuppliesFromBackpack = { backpack: Set<SchoolSupply> ->
            val newRemove = desktop?.schoolSuppliesInBackpack.orEmpty() - backpack
            desktop?.takeSchoolSuppliesFromBackpack(newRemove)
                ?.also {
                    runBlocking {
                        repository.takeSchoolSuppliesFromBackpack(
                            newRemove.map { supply -> supply.rfidCode }.toSet()
                        )
                    }
                }
        }

        val updateBackpack = { backpack: Set<SchoolSupply> ->
            putSchoolSuppliesInBackpack(backpack)
            takeSchoolSuppliesFromBackpack(backpack)
        }

        val getSupplyInBackpack = { backpackRfid: Set<String> ->
            desktop?.schoolSupplies
                ?.filter { supply -> backpackRfid.contains(supply.rfidCode) }
                ?.toSet() ?: emptySet()
        }

        getDesktop({
            runBlocking {
                repository.subscribeToBackpack({
                    success(
                        it.conflate()
                            .map { backpackRfid ->
                                getSupplyInBackpack(backpackRfid)
                                    .also { backpack ->
                                        updateBackpack(backpack)
                                    }
                            }
                    )
                }, error)
            }
        }, error)
    }

    /**
     * Delete the desktop.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    suspend fun deleteDesktop(success: () -> Unit, error: (Exception) -> Unit) {
        desktop = null
        repository.deleteDesktop(success, error)
    }
}
