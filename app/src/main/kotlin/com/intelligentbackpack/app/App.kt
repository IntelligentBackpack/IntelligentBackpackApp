package com.intelligentbackpack.app

import android.app.Application
import com.intelligentbackpack.accessdomain.usecase.AccessUseCase
import com.intelligentbackpack.app.di.ServiceLocator
import com.intelligentbackpack.desktopdomain.usecase.DesktopUseCase
import com.intelligentbackpack.reminderdomain.usecase.ReminderUseCase
import com.intelligentbackpack.schooldomain.usecase.SchoolUseCase

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

    lateinit var schoolUseCase: SchoolUseCase
        private set

    lateinit var reminderUseCase: ReminderUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initializeRepository(this)
        accessUseCase = AccessUseCase(ServiceLocator.accessRepository)
        desktopUseCase = DesktopUseCase(accessUseCase, ServiceLocator.desktopRepository)
        schoolUseCase = SchoolUseCase(accessUseCase, ServiceLocator.schoolRepository)
        reminderUseCase = ReminderUseCase(
            accessUseCase,
            desktopUseCase,
            schoolUseCase,
            ServiceLocator.reminderRepository,
        )
        accessUseCase.onUserLogin = {
            desktopUseCase.downloadDesktop()
            schoolUseCase.downloadSchool()
            reminderUseCase.downloadReminder()
        }
        accessUseCase.onUserLogout = {
            desktopUseCase.logoutDesktop()
        }
        accessUseCase.onUserDelete = { desktopUseCase.deleteDesktop() }
    }
}
