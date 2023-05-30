package com.intelligentbackpack.app

import android.app.Application
import com.intelligentbackpack.app.di.ServiceLocator
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase

/**
 * Application class
 */
class App : Application() {
    /**
     * Access use case
     */
    lateinit var accessUseCase: AccessUseCase
        private set

    /**
     * Desktop use case
     */
    lateinit var desktopUseCase: DesktopUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initializeRepository(this)
        accessUseCase = AccessUseCase(ServiceLocator.accessRepository)
        desktopUseCase = DesktopUseCase(accessUseCase, ServiceLocator.desktopRepository)
        accessUseCase.onUserLogin = { desktopUseCase.downloadDesktop() }
        accessUseCase.onUserLogout = { desktopUseCase.logoutDesktop() }
        accessUseCase.onUserDelete = { desktopUseCase.deleteDesktop() }
    }
}
