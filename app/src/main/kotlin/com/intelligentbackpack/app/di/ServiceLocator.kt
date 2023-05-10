package com.intelligentbackpack.app.di

import android.content.Context
import com.intelligentbackpack.accessdata.datasource.AccessDomainRepositoryImpl
import com.intelligentbackpack.accessdata.datasource.AccessLocalDataSourceImpl
import com.intelligentbackpack.accessdata.datasource.AccessRemoteDataSourceImpl
import com.intelligentbackpack.accessdata.storage.UserStorageImpl
import com.intelligentbackpack.accessdomain.repository.AccessDomainRepository

object ServiceLocator {

    private const val accessUrl = "https://accessmicroservice.azurewebsites.net/"

    lateinit var accessRepository: AccessDomainRepository
        private set

    fun initializeRepository(context: Context) {
        val accessRemoteDataSource = AccessRemoteDataSourceImpl(accessUrl)
        val userStorage = UserStorageImpl(context)
        val accessLocalDataSource = AccessLocalDataSourceImpl(userStorage)
        accessRepository = AccessDomainRepositoryImpl(accessLocalDataSource, accessRemoteDataSource)
    }


}