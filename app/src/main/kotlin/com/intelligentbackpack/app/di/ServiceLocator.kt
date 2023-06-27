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
import com.intelligentbackpack.reminderdata.datasource.ReminderDomainRepositoryImpl
import com.intelligentbackpack.reminderdata.datasource.ReminderLocalDataSourceImpl
import com.intelligentbackpack.reminderdata.datasource.ReminderRemoteDataSourceImpl
import com.intelligentbackpack.reminderdata.db.ReminderDatabaseHelper
import com.intelligentbackpack.reminderdomain.repository.ReminderDomainRepository
import com.intelligentbackpack.schooldata.datasource.SchoolDomainRepositoryImpl
import com.intelligentbackpack.schooldata.datasource.SchoolLocalDataSourceImpl
import com.intelligentbackpack.schooldata.datasource.SchoolRemoteDataSourceImpl
import com.intelligentbackpack.schooldata.db.SchoolDatabaseHelper
import com.intelligentbackpack.schooldata.storage.SchoolStorageImpl
import com.intelligentbackpack.schooldomain.repository.SchoolDomainRepository

object ServiceLocator {

    private const val accessUrl = "https://accessmicroservice.azurewebsites.net/"
    private const val desktopUrl = "https://booksmicroservice.azurewebsites.net/"
    private const val backpackAssociate = "https://managebackpackservice.azurewebsites.net/"
    private const val calendarUrl = "https://calendarmicroservice.azurewebsites.net/"
    private const val desktopRealtime =
        "https://intelligentbackpack-d463a-default-rtdb.europe-west1.firebasedatabase.app/"

    lateinit var accessRepository: AccessDomainRepository
        private set

    lateinit var desktopRepository: DesktopDomainRepository
        private set

    lateinit var schoolRepository: SchoolDomainRepository
        private set

    lateinit var reminderRepository: ReminderDomainRepository
        private set

    fun initializeRepository(context: Context) {
        val accessRemoteDataSource = AccessRemoteDataSourceImpl(accessUrl, desktopUrl)
        val userStorage = UserStorageImpl(context)
        val accessLocalDataSource = AccessLocalDataSourceImpl(userStorage)
        accessRepository = AccessDomainRepositoryImpl(accessLocalDataSource, accessRemoteDataSource)
        val desktopStorage = DesktopStorageImpl(context)
        val desktopDatabase = DesktopDatabaseHelper.getDatabase(context)
        val desktopRemoteDataSource = DesktopRemoteDataSourceImpl(desktopUrl, backpackAssociate, desktopRealtime)
        val desktopLocalDataSource = DesktopLocalDataSourceImpl(desktopDatabase, desktopStorage)
        desktopRepository = DesktopDomainRepositoryImpl(desktopLocalDataSource, desktopRemoteDataSource)
        val schoolStorage = SchoolStorageImpl(context)
        val schoolDatabase = SchoolDatabaseHelper.getDatabase(context)
        val schoolRemoteDataSource = SchoolRemoteDataSourceImpl(calendarUrl)
        val schoolLocalDataSource = SchoolLocalDataSourceImpl(schoolDatabase, schoolStorage)
        schoolRepository = SchoolDomainRepositoryImpl(schoolRemoteDataSource, schoolLocalDataSource)
        val reminderRemoteDataSource = ReminderRemoteDataSourceImpl(calendarUrl)
        val reminderDatabase = ReminderDatabaseHelper.getDatabase(context)
        val reminderLocalDataSource = ReminderLocalDataSourceImpl(reminderDatabase)
        reminderRepository = ReminderDomainRepositoryImpl(reminderRemoteDataSource, reminderLocalDataSource)
    }
}
