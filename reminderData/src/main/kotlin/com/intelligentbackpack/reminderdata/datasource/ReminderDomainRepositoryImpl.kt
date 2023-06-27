package com.intelligentbackpack.reminderdata.datasource

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.reminderdata.adapter.LessonAdapter.fromDBToRemote
import com.intelligentbackpack.reminderdata.adapter.LessonAdapter.fromDomainToDB
import com.intelligentbackpack.reminderdata.adapter.LessonAdapter.fromRemoteToDB
import com.intelligentbackpack.reminderdata.adapter.ReminderAdapter.fromDBToDomain
import com.intelligentbackpack.reminderdata.adapter.ReminderAdapter.fromDomainToDB
import com.intelligentbackpack.reminderdata.adapter.ReminderAdapter.fromRemoteToDB
import com.intelligentbackpack.reminderdata.adapter.SubjectAdapter.fromRemoteToDB
import com.intelligentbackpack.reminderdomain.entitites.Reminder
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson
import com.intelligentbackpack.reminderdomain.repository.ReminderDomainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.intelligentbackpack.reminderdomain.entitites.Reminder as DomainReminder

/**
 * Reminder domain repository implementation
 *
 * @param remoteDataSource the remote data source
 * @param localDataSource the local data source
 */
class ReminderDomainRepositoryImpl(
    private val remoteDataSource: ReminderRemoteDataSource,
    private val localDataSource: ReminderLocalDataSource,
) : ReminderDomainRepository {
    override suspend fun downloadReminder(user: User): Unit =
        withContext(Dispatchers.IO) {
            localDataSource.deleteData()
            val year = remoteDataSource.downloadYear()
            val subjects = remoteDataSource.downloadSubjects()
            localDataSource.insertSubjects(subjects.map { it.fromRemoteToDB() })
            val lessons = if (user.role == Role.STUDENT) {
                remoteDataSource.downloadLessonsForStudent(user.email, year)
            } else {
                remoteDataSource.downloadLessonsForProfessor(user.email, year)
            }
            if (lessons.isNotEmpty()) {
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

    override suspend fun getReminder(): Reminder =
        withContext(Dispatchers.IO) {
            val lessons = localDataSource.getLessons()
            val subjects = localDataSource.getSubjects()
            DomainReminder.create(
                localDataSource.getReminders().map { it.fromDBToDomain(lessons, subjects) }.toSet(),
            )
        }

    override suspend fun addBookForLesson(reminderForLesson: ReminderForLesson, user: User) =
        withContext(Dispatchers.IO) {
            val subjects = localDataSource.getSubjects()
            val abstractLessonDB = reminderForLesson.lesson.fromDomainToDB(subjects)
            val lesson = localDataSource.getLesson(
                abstractLessonDB.day,
                abstractLessonDB.startTime,
                abstractLessonDB.endTime,
                abstractLessonDB.fromDate,
                abstractLessonDB.toDate,
            )
            if (lesson != null) {
                localDataSource.saveReminder(
                    reminderForLesson.fromDomainToDB(lesson),
                )
                remoteDataSource.createNewReminderForLesson(
                    user.email,
                    lesson.fromDBToRemote(),
                    reminderForLesson.isbn,
                )
            }
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
