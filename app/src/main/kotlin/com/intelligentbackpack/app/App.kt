package com.intelligentbackpack.app

import android.app.Application
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.app.di.ServiceLocator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initializeRepository(this)
        accessUseCase = AccessUseCase(ServiceLocator.accessRepository)
    }

    lateinit var accessUseCase : AccessUseCase
        private set
}
