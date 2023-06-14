package com.intelligentbackpack.reminderdata.datasource

import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.reminderdata.adapter.LessonAdapter.fromRemoteToDB
import com.intelligentbackpack.reminderdata.adapter.ReminderAdapter.fromDBToDomain
import com.intelligentbackpack.reminderdata.adapter.ReminderAdapter.fromRemoteToDB
import com.intelligentbackpack.reminderdata.adapter.SubjectAdapter.fromRemoteToDB
import com.intelligentbackpack.reminderdomain.entitites.Reminder
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson
import com.intelligentbackpack.reminderdomain.repository.ReminderDomainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.intelligentbackpack.reminderdomain.entitites.Reminder as DomainReminder

class ReminderDomainRepositoryImpl(
    private val remoteDataSource: ReminderRemoteDataSource,
    private val localDataSource: ReminderLocalDataSource,
) : ReminderDomainRepository {
    override suspend fun downloadReminder(user: User): Unit =
        withContext(Dispatchers.IO) {
            localDataSource.deleteData()
            val year = remoteDataSource.downloadYear()
            localDataSource.saveYear(year)
            val subjects = remoteDataSource.downloadSubjects()
            localDataSource.insertSubjects(subjects.map { it.fromRemoteToDB() })
            val lessons = remoteDataSource.downloadLessonsForStudent(user.email, year)
            if (lessons.isNotEmpty()) {
                localDataSource.saveCalendarId(lessons[0].idCalendario)
                localDataSource.saveLessons(lessons.map { it.fromRemoteToDB() })
                lessons.map { lesson ->
                    val dbLesson = lesson.fromRemoteToDB()
                    localDataSource
                        .saveReminders(
                            remoteDataSource
                                .downloadBooksForLesson(lesson)
                                .mapNotNull { booksForLesson ->
                                    localDataSource.getLesson(
                                        dbLesson.day,
                                        dbLesson.startTime,
                                        dbLesson.endTime,
                                        dbLesson.fromDate,
                                        dbLesson.toDate,
                                    )?.let {
                                        booksForLesson
                                            .fromRemoteToDB(
                                                it,
                                            )
                                    }
                                },
                        )
                }
            }
        }

    override suspend fun getReminder(): Reminder {
        val lessons = localDataSource.getLessons()
        val subjects = localDataSource.getSubjects()
        return DomainReminder.create(
            localDataSource.getReminders().map { it.fromDBToDomain(lessons, subjects) }.toSet(),
        )
    }

    override suspend fun addBookForLesson(reminderForLesson: ReminderForLesson, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun removeBookForLesson(reminderForLesson: ReminderForLesson, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun changeBookForLesson(
        reminderForLesson: ReminderForLesson,
        newReminderForLesson: ReminderForLesson,
        user: User,
    ) {
        TODO("Not yet implemented")
    }
}
