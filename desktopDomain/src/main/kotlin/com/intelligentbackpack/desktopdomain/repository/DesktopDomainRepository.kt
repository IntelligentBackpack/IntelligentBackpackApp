package com.intelligentbackpack.desktopdomain.repository

import com.intelligentbackpack.desktopdomain.entities.Desktop
import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.desktopdomain.entities.Subject

/**
 * Interface for the desktop domain repository.
 */
interface DesktopDomainRepository {

    /**
     * Gets the desktop.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    fun getDesktop(success: (Desktop) -> Unit, error: (Exception) -> Unit)

    /**
     * Gets all the subjects.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    fun getSubjects(success: (Set<Subject>) -> Unit, error: (Exception) -> Unit)

    /**
     * Adds a school supply to the desktop.
     *
     * @param schoolSupply The school supply to add.
     * @param success The success callback with all the school supplies.
     * @param error The error callback.
     */
    fun addSchoolSupply(schoolSupply: SchoolSupply, success: (Set<SchoolSupply>) -> Unit, error: (Exception) -> Unit)

    /**
     * Replace old school supplies with new ones.
     *
     * @param newSchoolSupplies The new school supplies.
     * @param oldSupplies The old school supplies.
     * @param success The success callback with all the school supplies.
     * @param error The error callback.
     */
    fun replaceSchoolSupplies(
        newSchoolSupplies: Set<SchoolSupply>,
        oldSupplies: Set<SchoolSupply>,
        success: (Set<SchoolSupply>) -> Unit,
        error: (Exception) -> Unit
    )

    /**
     * Adds a subject to a school supply.
     *
     * @param schoolSupply The school supply.
     * @param subject The subject.
     * @param success The success callback with the school supply with the subject added.
     * @param error The error callback.
     */
    fun addSubjectToSchoolSupply(
        schoolSupply: SchoolSupply,
        subject: Subject,
        success: (SchoolSupply) -> Unit,
        error: (Exception) -> Unit
    )

    /**
     * Gets a school supply by its RFID.
     *
     * @param rfid The RFID of the school supply.
     * @param success The success callback with the school supply.
     * @param error The error callback.
     */
    fun getSchoolSupply(rfid: String, success: (SchoolSupply) -> Unit, error: (Exception) -> Unit)

    /**
     * Gets a subject by its name.
     *
     * @param subject The name of the subject.
     * @param success The success callback with all the subjects.
     * @param error The error callback.
     */
    fun addSubject(subject: Subject, success: (Set<Subject>) -> Unit, error: (Exception) -> Unit)

    /**
     * Gets all the school supplies of a subject.
     *
     * @param subject The subject.
     * @param success The success callback with all the school supplies with the subject.
     * @param error The error callback.
     */
    fun getAllSchoolSuppliesOfSubject(
        subject: Subject,
        success: (Set<SchoolSupply>) -> Unit,
        error: (Exception) -> Unit
    )

    /**
     * Deletes the desktop and all its school supplies.
     *
     * @param success The success callback.
     * @param error The error callback.
     */
    fun deleteDesktop(success: () -> Unit, error: (Exception) -> Unit)
}
