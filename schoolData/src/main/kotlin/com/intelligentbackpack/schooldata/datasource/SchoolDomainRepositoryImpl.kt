package com.intelligentbackpack.schooldata.datasource

import com.intelligentbackpack.accessdomain.entities.Role
import com.intelligentbackpack.accessdomain.entities.User
import com.intelligentbackpack.schooldata.adapter.ClassAdapter.fromDBToDomain
import com.intelligentbackpack.schooldata.adapter.LessonAdapter.fromDBToDomain
import com.intelligentbackpack.schooldata.adapter.LessonAdapter.fromRemoteToDB
import com.intelligentbackpack.schooldata.adapter.ProfessorAdapter.fromDBToDomain
import com.intelligentbackpack.schooldata.adapter.ProfessorAdapter.professorFromRemoteToDB
import com.intelligentbackpack.schooldata.adapter.StudentAdapter.fromAccessToSchool
import com.intelligentbackpack.schooldata.adapter.SubjectAdapter.fromRemoteToDB
import com.intelligentbackpack.schooldata.db.entities.SchoolClass
import com.intelligentbackpack.schooldata.db.entities.Teach
import com.intelligentbackpack.schooldomain.entities.School
import com.intelligentbackpack.schooldomain.entities.calendar.SchoolCalendar
import com.intelligentbackpack.schooldomain.entities.calendar.alteration.AlterationEvent
import com.intelligentbackpack.schooldomain.repository.SchoolDomainRepository

class SchoolDomainRepositoryImpl(
    private val remoteDataSource: SchoolRemoteDataSource,
    private val localDataSource: SchoolLocalDataSource,
) : SchoolDomainRepository {
    override suspend fun getSchool(user: User): School {
        val schoolName = localDataSource.getSchool()
        val schoolCity = localDataSource.getCity()
        val school = School.create(schoolName, schoolCity)
        val teaches = localDataSource.getTeaches()
        val teachesByProfessor = teaches.groupBy { it.professorEmail }
        val lessons = localDataSource.getLessons()
        val subjects = localDataSource.getSubjects()
        val year = localDataSource.getYear()
        val classes = localDataSource.getClasses().map { it.fromDBToDomain(school) }.let {
            if (user.role == Role.STUDENT) {
                val userClass = localDataSource.getUserClass()
                it.map { schoolClass ->
                    if (schoolClass.name == userClass) {
                        schoolClass.addStudent(user.fromAccessToSchool(schoolClass))
                    } else {
                        schoolClass
                    }
                }
            } else {
                it
            }
        }
        val professors = localDataSource.getProfessors().map { it.fromDBToDomain() }
            .map { professor ->
                teachesByProfessor[professor.email]?.let { teach ->
                    teach.groupBy { it.schoolClass }.entries.fold(professor) { acc, entry ->
                        val className = classes.first { it.name == entry.key }
                        val subjectsForClass = entry.value.map {
                            subjects.first { subject -> subject.subjectId == it.subjectId }.name
                        }.toSet()
                        acc.addProfessorToClass(className, subjectsForClass)
                    }
                } ?: professor
            }
        val domainLessons = lessons.map { lesson ->
            lesson.fromDBToDomain(
                professors.first { professor ->
                    professor.email == teaches.first {
                        lesson.teachId == it.id
                    }.professorEmail
                },
                classes.first { schoolClass ->
                    schoolClass.name == teaches.first {
                        lesson.teachId == it.id
                    }.schoolClass
                },

            )
        }
        val calendar = SchoolCalendar.create(year)
        return classes.fold(school) { acc, schoolClass ->
            acc.addClass(schoolClass)
        }.let { schoolWithClasses ->
            professors.fold(schoolWithClasses) { acc, professor ->
                acc.addProfessor(professor)
            }
        }.let { schoolWithClassesAndProfessors ->
            calendar.addLessons(domainLessons.toSet()).let {
                schoolWithClassesAndProfessors.replaceCalendar(it)
            }
        }
    }

    private suspend fun downloadForStudent(user: User, year: String) {
        val student = remoteDataSource.downloadStudent(user.email, year)
        localDataSource.saveSchool(student.getInsitutesName(0), student.getInsitutesCitta(0))
        localDataSource.saveClass(student.getClasses(0))
        val lessons = remoteDataSource.downloadLessonsForStudent(user.email, year)
        lessons.groupBy { it.idCalendario }.forEach { (calendar, lessonsForClass) ->
            val className = remoteDataSource.getClass(lessonsForClass.first())
            localDataSource.insertClass(SchoolClass(className, calendar))
            lessonsForClass.groupBy { it.professore }.forEach { (professor, lessonsForProfessor) ->
                val professorInfo = remoteDataSource.downloadProfessor(professor, year)
                localDataSource.insertProfessor(professorInfo.professorFromRemoteToDB())
                lessonsForProfessor.groupBy { it.materia }.forEach { (subject, lessonForSubject) ->
                    val id = localDataSource.insertTeach(Teach(0, professor, className, subject))
                    lessonForSubject.forEach {
                        localDataSource.insertLesson(it.fromRemoteToDB(id.toInt()))
                    }
                }
            }
        }
    }

    private suspend fun downloadForProfessor(user: User, year: String) {
        val professor = remoteDataSource.downloadProfessor(user.email, year)
        localDataSource.insertProfessor(professor.professorFromRemoteToDB())
        localDataSource.saveSchool(professor.getInsitutesName(0), professor.getInsitutesCitta(0))
        val lessons = remoteDataSource.downloadLessonsForProfessor(user.email, year)
        lessons.groupBy { it.idCalendario }.forEach { (calendar, lessonsForClass) ->
            val className = remoteDataSource.getClass(lessonsForClass.first())
            localDataSource.insertClass(SchoolClass(className, calendar))
            lessonsForClass.groupBy { it.materia }.forEach { (subject, lessonForSubject) ->
                val id = localDataSource.insertTeach(Teach(0, user.email, className, subject))
                lessonForSubject.forEach {
                    localDataSource.insertLesson(it.fromRemoteToDB(id.toInt()))
                }
            }
        }
    }

    override suspend fun downloadSchool(user: User) {
        val year = remoteDataSource.downloadYear()
        localDataSource.saveYear(year)
        val subjects = remoteDataSource.downloadSubjects()
        localDataSource.insertSubjects(subjects.map { it.fromRemoteToDB() })
        when (user.role) {
            Role.STUDENT ->
                downloadForStudent(user, year)

            Role.PROFESSOR ->
                downloadForProfessor(user, year)

            else -> {}
        }
    }

    override suspend fun addAlterationEvent(alterationEvent: AlterationEvent) {
        TODO("Not yet implemented")
    }
}
