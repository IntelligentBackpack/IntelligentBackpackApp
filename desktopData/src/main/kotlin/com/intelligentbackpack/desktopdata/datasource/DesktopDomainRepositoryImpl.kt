package com.intelligentbackpack.desktopdata.datasource

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.desktopdomain.entities.Book
import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.exception.BackpackNotAssociatedException
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DesktopDomainRepositoryImpl(
    private val desktopLocalDataSource: DesktopLocalDataSource,
    private val desktopRemoteDataSource: DesktopRemoteDataSource,
) : DesktopDomainRepository {

    private fun updateBackpackContent(rfidInsert: Set<String>) {
        desktopLocalDataSource.putSchoolSuppliesInBackpack(rfidInsert)
        val allSupplyRfid = desktopLocalDataSource.getAllSchoolSupplies().map { it.rfidCode }.toSet()
        desktopLocalDataSource.takeSchoolSuppliesFromBackpack(allSupplyRfid - rfidInsert)
    }

    override suspend fun downloadDesktop(user: User): Desktop =
        withContext(Dispatchers.IO) {
            desktopRemoteDataSource.getDesktop(user)
                .also { remoteDesktop ->
                    remoteDesktop.schoolSupplies.forEach {
                        desktopLocalDataSource.addSchoolSupply(it)
                    }
                    remoteDesktop.backpack?.let { backpack ->
                        updateBackpackContent(remoteDesktop.schoolSuppliesInBackpack.map { it.rfidCode }.toSet())
                        desktopLocalDataSource.associateBackpack(backpack)
                    } ?: run {
                        updateBackpackContent(emptySet())
                        desktopLocalDataSource.getBackpack()?.let {
                            desktopLocalDataSource.disassociateBackpack()
                        }
                    }
                }
        }

    override suspend fun getDesktop(user: User): Desktop =
        withContext(Dispatchers.IO) {
            Desktop.create(
                desktopLocalDataSource.getAllSchoolSupplies(),
                desktopLocalDataSource.getSchoolSupplyInBackpack(),
                desktopLocalDataSource.getBackpack(),
            )
        }

    override suspend fun addSchoolSupply(user: User, schoolSupply: SchoolSupply) =
        withContext(Dispatchers.IO) {
            desktopLocalDataSource.addSchoolSupply(schoolSupply)
            desktopRemoteDataSource.addSchoolSupply(user, schoolSupply)
            desktopLocalDataSource.getAllSchoolSupplies()
        }

    override suspend fun getBook(isbn: String): Book? =
        withContext(Dispatchers.IO) {
            desktopLocalDataSource.getBook(isbn)
                ?: desktopRemoteDataSource.getBook(isbn)
                    ?.also { remoteBook ->
                        desktopLocalDataSource.addBook(remoteBook)
                    }
        }

    override suspend fun logoutDesktop(user: User) =
        withContext(Dispatchers.IO) {
            desktopLocalDataSource.deleteDesktop()
        }

    override suspend fun subscribeToBackpack(user: User): Flow<Set<String>> =
        withContext(Dispatchers.IO) {
            desktopLocalDataSource.getBackpack()?.let { backpack ->
                desktopRemoteDataSource.subscribeToBackpackChanges(user, backpack)
                    .flowOn(Dispatchers.IO)
                    .map { it.getOrDefault(setOf()) }
                    .map { rfidCodes ->
                        val taken = desktopLocalDataSource
                            .getSchoolSupplyInBackpack().map { it.rfidCode }.toSet() - rfidCodes
                        desktopLocalDataSource.putSchoolSuppliesInBackpack(rfidCodes)
                        desktopLocalDataSource.takeSchoolSuppliesFromBackpack(taken)
                        rfidCodes
                    }
            } ?: throw BackpackNotAssociatedException()
        }

    override suspend fun associateBackpack(user: User, hash: String) =
        withContext(Dispatchers.IO) {
            val newHash = desktopRemoteDataSource.associateBackpack(user, hash)
            desktopLocalDataSource.associateBackpack(newHash)
            newHash
        }

    override suspend fun disassociateBackpack(user: User, hash: String) =
        withContext(Dispatchers.IO) {
            desktopRemoteDataSource.disassociateBackpack(user, hash)
            desktopLocalDataSource.disassociateBackpack()
            desktopLocalDataSource.removeAllSchoolSuppliesFromBackpack()
            hash
        }

    override suspend fun deleteDesktop(user: User) =
        withContext(Dispatchers.IO) {
            desktopLocalDataSource.getBackpack()?.let {
                desktopRemoteDataSource.disassociateBackpack(user, it)
            }
            desktopRemoteDataSource.deleteDesktop(user)
            desktopLocalDataSource.deleteDesktop()
        }
}
