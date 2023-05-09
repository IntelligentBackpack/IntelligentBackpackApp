package com.intelligentbackpack.app.di

import android.content.Context
import com.intelligentbackpack.accessdata.datasource.AccessDomainRepositoryImpl
import com.intelligentbackpack.accessdata.datasource.AccessLocalDataStorageImpl
import com.intelligentbackpack.accessdata.datasource.AccessRemoteDataStorageImpl
import com.intelligentbackpack.accessdata.storage.UserStorageImpl
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

object ServiceLocator {

    private const val accessUrl = "https://accessmicroservice.azurewebsites.net/"

    lateinit var accessRepository: AccessDomainRepository
        private set

    fun initializeRepository(context: Context) {
        val accessRemoteDataStorage = AccessRemoteDataStorageImpl(accessUrl)
        val userStorage = UserStorageImpl(context)
        val accessLocalDataStorage = AccessLocalDataStorageImpl(userStorage)
        accessRepository = AccessDomainRepositoryImpl(accessLocalDataStorage, accessRemoteDataStorage)
    }


}