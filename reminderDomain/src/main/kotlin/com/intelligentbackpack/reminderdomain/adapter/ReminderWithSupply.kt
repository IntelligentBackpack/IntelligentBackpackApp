package com.intelligentbackpack.reminderdomain.adapter

import com.intelligentbackpack.desktopdomain.entities.SchoolSupply
import com.intelligentbackpack.reminderdomain.entitites.ReminderForLesson

/**
 * Reminder with supply.
 *
 * @param supply supply.
 * @param reminder reminder.
 */
data class ReminderWithSupply(
    val supply: SchoolSupply,
    val reminder: ReminderForLesson,
)
