package com.tech24et.tech24technician.components

import android.location.Location
import kotlin.math.max
import kotlin.math.roundToInt

enum class Priority { URGENT, NORMAL }

/**
 * Case lifecycle, in order. The order matters: the timeline on the case details screen
 * is just `CaseStatus.entries`, and "next step" is `status.next`.
 *
 * [actionLabel] is the label of the button that moves the case INTO this status.
 */
enum class CaseStatus(
    val label: String,
    val chipLabel: String,
    val timelineTitle: String,
    val actionLabel: String?,
) {
    NEW("New case", "New", "Case Created", null),
    ACCEPTED("Accepted", "Accepted", "Case Accepted", "Accept case"),
    ON_THE_WAY("On the way", "On the way", "Started Travel", "Start travel"),
    ARRIVED("Arrived", "Arrived", "Arrived", "I've arrived"),
    IN_PROGRESS("In progress", "In progress", "Work Started", "Start work"),
    COMPLETED("Completed", "Completed", "Work Completed", "Complete case");

    val next: CaseStatus? get() = entries.getOrNull(ordinal + 1)
}

/** One row in the case audit trail: what happened, when, and where the technician was. */
data class StatusEvent(
    val status: CaseStatus,
    val timestamp: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracyMeters: Float? = null,
)

data class ServiceCase(
    val id: String,
    val bank: String,
    val branch: String,
    val issue: String,
    val category: String,
    val priority: Priority,
    val reportedAt: Long,
    val contactName: String,
    val contactPhone: String,
    val address: String,
    val description: String,
    val destLatitude: Double,
    val destLongitude: Double,
    /** Used until we get a GPS fix. */
    val fallbackDistanceKm: Double,
    val status: CaseStatus,
    val history: List<StatusEvent>,
) {
    val title: String get() = "$bank — $branch"
    val isOpen: Boolean get() = status != CaseStatus.COMPLETED
    fun eventFor(status: CaseStatus): StatusEvent? = history.lastOrNull { it.status == status }
}

enum class NotificationType { URGENT_CASE, ROUTE, INFO }

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val createdAt: Long,
    val read: Boolean,
    val caseId: String? = null,
)

data class Technician(
    val name: String,
    val role: String,
    val badgeId: String,
    val completedCases: Int,
    val avgResolutionMinutes: Int,
    val successRatePercent: Int,
) {
    val firstName: String get() = name.substringBefore(' ')
    val initial: String get() = name.take(1).uppercase()
}

data class LocationFix(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val timestamp: Long,
)

data class TechnicianUiState(
    val technician: Technician,
    val cases: List<ServiceCase>,
    val notifications: List<AppNotification>,
    val location: LocationFix? = null,
    val isOnline: Boolean = true,
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val locationTrackingEnabled: Boolean = true,
    /** Case id currently being updated (waiting for GPS / network). Used to show a spinner. */
    val updatingCaseId: String? = null,
) {
    /** The case the technician should be working on: open cases, urgent first, then oldest first. */
    val activeCase: ServiceCase?
        get() = cases.filter { it.isOpen }
            .sortedWith(
                compareByDescending<ServiceCase> { it.priority == Priority.URGENT }
                    .thenBy { it.reportedAt }
            )
            .firstOrNull()

    val unreadCount: Int get() = notifications.count { !it.read }

    /** Straight-line distance from the technician to the case. Swap for a routing API if you need road distance. */
    fun distanceKm(case: ServiceCase): Double {
        val fix = location ?: return case.fallbackDistanceKm
        val out = FloatArray(1)
        Location.distanceBetween(
            fix.latitude, fix.longitude,
            case.destLatitude, case.destLongitude,
            out,
        )
        return out[0] / 1000.0
    }

    fun gpsQuality(): String? = location?.let {
        when {
            it.accuracyMeters <= 25f -> "High"
            it.accuracyMeters <= 75f -> "Medium"
            else -> "Low"
        }
    }
}

/** Rough city driving estimate (~18 km/h average in Addis traffic). */
fun estimateTravelMinutes(distanceKm: Double): Int =
    max(1, (distanceKm / 18.0 * 60).roundToInt())
