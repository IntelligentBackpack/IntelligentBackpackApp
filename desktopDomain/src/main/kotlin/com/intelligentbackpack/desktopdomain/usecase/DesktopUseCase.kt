package com.intelligentbackpack.desktopdomain.usecase

import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.Subject
import com.intelligentbackpack.desktopdomain.repository.DesktopDomainRepository

/**
 * Use case for the desktop domain.
 */
class DesktopUseCase(private val repository: DesktopDomainRepository) {
    /**
     * Gets the desktop.
     *
     * @param success the success callback with the desktop.
     * @param error the error callback.
     */
    suspend fun getDesktop(success: (Desktop) -> Unit, error: (Exception) -> Unit) =
        repository.getDesktop(success, error)

    /**
     * Gets all the subjects.
     *
     * @param success The success callback with all the subjects.
     * @param error The error callback.
     */
    suspend fun getSubjects(success: (Set<Subject>) -> Unit, error: (Exception) -> Unit) =
        repository.getSubjects(success, error)

    /**
     * Adds a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @param success The success callback with all the school supplies.
     * @param error The error callback.
     */
    suspend fun addSchoolSupply(
        schoolSupply: SchoolSupply,
        success: (Set<SchoolSupply>) -> Unit,
        error: (Exception) -> Unit
    ) =
        repository.addSchoolSupply(schoolSupply, success, error)

    /**
     * Replace old school supplies with new ones.
     *
     * @param newSchoolSupplies The new school supplies that replace the old ones.
     * @param oldSupplies The old school supplies.
     * @param success The success callback with all the school supplies.
     * @param error The error callback.
     */
    suspend fun replaceSchoolSupplies(
        newSchoolSupplies: Set<SchoolSupply>,
        oldSupplies: Set<SchoolSupply>,
        success: (Set<SchoolSupply>) -> Unit,
        error: (Exception) -> Unit
    ) = repository.replaceSchoolSupplies(newSchoolSupplies, oldSupplies, success, error)

    /**
     * Adds a subject to a school supply.
     *
     * @param schoolSupply The school supply.
     * @param subject The subject.
     * @param success The success callback with the school supply with the subject added.
     * @param error The error callback.
     */
    suspend fun addSubjectToSchoolSupply(
        schoolSupply: SchoolSupply,
        subject: Subject,
        success: (SchoolSupply) -> Unit,
        error: (Exception) -> Unit
    ) = repository.addSubjectToSchoolSupply(schoolSupply, subject, success, error)

    /**
     * Gets the school supply given the rfid.
     *
     * @param rfid The rfid.
     * @param success The success callback with the school supply.
     * @param error The error callback.
     */
    suspend fun getSchoolSupply(rfid: String, success: (SchoolSupply) -> Unit, error: (Exception) -> Unit) =
        repository.getSchoolSupply(rfid, success, error)

    /**
     * Add subject to the desktop.
     *
     * @param subject The subject.
     * @param success The success callback with the subjects.
     * @param error The error callback.
     */
    suspend fun addSubject(subject: Subject, success: (Set<Subject>) -> Unit, error: (Exception) -> Unit) =
        repository.addSubject(subject, success, error)

    /**
     * Get all school supplies of a subject.
     *
     * @param subject The subject.
     * @param success The success callback with the school supplies.
     * @param error The error callback.
     */
    suspend fun getSuppliesForSubject(
        subject: Subject,
        success: (Set<SchoolSupply>) -> Unit,
        error: (Exception) -> Unit
    ) =
        repository.getAllSchoolSuppliesOfSubject(subject, success, error)

    /**
     * Delete the desktop.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    suspend fun deleteDesktop(success: () -> Unit, error: (Exception) -> Unit) =
        repository.deleteDesktop(success, error)
}
