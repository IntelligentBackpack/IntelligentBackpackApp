package com.intelligentbackpack.app.di

import android.content.Context
import com.intelligentbackpack.accessdata.datasource.AccessDomainRepositoryImpl
import com.intelligentbackpack.accessdata.datasource.AccessLocalDataSourceImpl
import com.intelligentbackpack.accessdata.datasource.AccessRemoteDataSourceImpl
import com.intelligentbackpack.accessdata.storage.UserStorageImpl
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository
import com.intelligentbackpack.desktopdata.datasource.DesktopDomainRepositoryImpl
import com.intelligentbackpack.desktopdata.datasource.DesktopLocalDataSourceImpl
import com.intelligentbackpack.desktopdata.datasource.DesktopRemoteDataSourceImpl
import com.intelligentbackpack.desktopdata.db.DesktopDatabaseHelper
import com.intelligentbackpack.desktopdata.storage.DesktopStorageImpl
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository

object ServiceLocator {

    private const val accessUrl = "https://accessmicroservice.azurewebsites.net/"
    private const val desktopUrl = "https://booksmicroservice.azurewebsites.net/"
    private const val backpackAssociate = "http://managebackpackservice.azurewebsites.net/"
    private const val desktopRealtime = "https://intelligentbackpackapp-default-rtdb.europe-west1.firebasedatabase.app/"

    lateinit var accessRepository: AccessDomainRepository
        private set

    lateinit var desktopRepository: DesktopDomainRepository
        private set

    fun initializeRepository(context: Context) {
        val accessRemoteDataSource = AccessRemoteDataSourceImpl(accessUrl, desktopUrl)
        val userStorage = UserStorageImpl(context)
        val accessLocalDataSource = AccessLocalDataSourceImpl(userStorage)
        accessRepository = AccessDomainRepositoryImpl(accessLocalDataSource, accessRemoteDataSource)
        val desktopStorage = DesktopStorageImpl(context)
        val database = DesktopDatabaseHelper.getDatabase(context)
        val desktopRemoteDataSource = DesktopRemoteDataSourceImpl(desktopUrl, backpackAssociate, desktopRealtime)
        val desktopLocalDataSource = DesktopLocalDataSourceImpl(database, desktopStorage)
        desktopRepository = DesktopDomainRepositoryImpl(desktopLocalDataSource, desktopRemoteDataSource)
    }
}
