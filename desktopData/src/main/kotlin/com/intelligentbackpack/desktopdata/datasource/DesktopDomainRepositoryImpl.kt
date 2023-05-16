package com.intelligentbackpack.desktopdata.datasource

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DesktopDomainRepositoryImpl(
    private val desktopLocalDataSource: DesktopLocalDataSource,
    private val desktopRemoteDataSource: DesktopRemoteDataSource,
) : DesktopDomainRepository {

    override suspend fun getDesktop(user: User, success: (Desktop) -> Unit, error: (Exception) -> Unit) {
        try {
            desktopRemoteDataSource.getDesktop(user)
                .let { remoteDesktop ->
                    remoteDesktop.schoolSupplies.forEach {
                        desktopLocalDataSource.addSchoolSupply(it)
                    }.let {
                        Desktop.create(
                            desktopLocalDataSource.getAllSchoolSupplies(),
                            desktopLocalDataSource.getSchoolSupplyInBackpack()
                        )
                    }.also { desktop ->
                        desktop.backpack?.let { backpack ->
                            desktopLocalDataSource
                                .putSchoolSuppliesInBackpack(
                                    desktop.schoolSuppliesInBackpack.map { it.rfidCode }
                                        .toSet()
                                )
                            desktopLocalDataSource
                                .takeSchoolSuppliesFromBackpack(
                                    (desktop.schoolSupplies - desktop.schoolSuppliesInBackpack)
                                        .map { it.rfidCode }
                                        .toSet()
                                )
                            desktopLocalDataSource.associateBackpack(backpack)
                        }
                        success(desktop)
                    }
                }
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun addSchoolSupply(
        user: User,
        schoolSupply: SchoolSupply,
        success: (Set<SchoolSupply>) -> Unit,
        error: (Exception) -> Unit
    ) {
        try {
            desktopLocalDataSource.addSchoolSupply(schoolSupply)
            desktopRemoteDataSource.addSchoolSupply(user, schoolSupply)
            success(desktopLocalDataSource.getAllSchoolSupplies())
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun getBook(isbn: String, success: (Book?) -> Unit, error: (Exception) -> Unit) {
        try {
            desktopLocalDataSource
                .getBook(isbn)
                ?.let { success(it) }
                ?: run {
                    success(
                        desktopRemoteDataSource.getBook(isbn)
                            ?.also { remoteBook ->
                                desktopLocalDataSource
                                    .addBook(remoteBook)
                            })
                }
        } catch (e: Exception) {
            error(e)
        }
    }

    override suspend fun logoutDesktop(user: User, success: () -> Unit, error: (Exception) -> Unit) {
        try {
            desktopLocalDataSource.deleteDesktop()
            success()
        } catch (e: Exception) {
            error(e)
        }
    }

    override fun subscribeToBackpack(user: User): Flow<Set<String>> {
        val backpack = desktopLocalDataSource.getBackpack()
        return desktopRemoteDataSource.subscribeToBackpackChanges(user, backpack).map { it.getOrDefault(setOf()) }
    }

    override suspend fun putSchoolSuppliesInBackpack(
        rfid: Set<String>,
    ) {
        desktopLocalDataSource.putSchoolSuppliesInBackpack(rfid)
    }

    override suspend fun takeSchoolSuppliesFromBackpack(
        rfid: Set<String>,
    ) {
        desktopLocalDataSource.takeSchoolSuppliesFromBackpack(rfid)
    }

    override suspend fun associateBackpack(user: User, hash: String, success: () -> Unit, error: (Exception) -> Unit) {
        try {
            desktopRemoteDataSource.associateBackpack(user, hash)
            desktopLocalDataSource.associateBackpack(hash)
            success()
        } catch (e: Exception) {
            error(e)
        }
    }
}
