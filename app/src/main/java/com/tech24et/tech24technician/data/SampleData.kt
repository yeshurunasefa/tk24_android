package com.tech24et.tech24technician.data

import com.tech24et.tech24technician.components.AppNotification
import com.tech24et.tech24technician.components.CaseStatus
import com.tech24et.tech24technician.components.NotificationType
import com.tech24et.tech24technician.components.Priority
import com.tech24et.tech24technician.components.ServiceCase
import com.tech24et.tech24technician.components.StatusEvent
import com.tech24et.tech24technician.components.Technician
import com.tech24et.tech24technician.components.TechnicianUiState

/** Placeholder data that matches the mockups. Replace with your backend / Room repository. */
object SampleData {

    fun initialState(): TechnicianUiState {
        val now = System.currentTimeMillis()
        val minute = 60_000L

        val urgentCase = ServiceCase(
            id = "CASE-2026-0842",
            bank = "Awash Bank",
            branch = "Bulbula Branch",
            issue = "ATM dispenser not working",
            category = "ATM & Cash Handling",
            priority = Priority.URGENT,
            reportedAt = now - 25 * minute,
            contactName = "Mekdes Tesfaye",
            contactPhone = "+251 911 246 810",
            address = "Bulbula, Addis Ababa",
            description = "ATM dispenser is not dispensing cash. Customer reports repeated transaction failures.",
            destLatitude = 8.9650,
            destLongitude = 38.7740,
            fallbackDistanceKm = 2.4,
            status = CaseStatus.NEW,
            history = listOf(StatusEvent(CaseStatus.NEW, now - 25 * minute)),
        )

        return TechnicianUiState(
            technician = Technician(
                name = "Wubet Tesfaye",
                role = "Field Service Technician",
                badgeId = "WB-2048",
                completedCases = 98,
                avgResolutionMinutes = 42,
                successRatePercent = 96,
            ),
            cases = listOf(urgentCase),
            notifications = listOf(
                AppNotification(
                    id = "n1",
                    type = NotificationType.URGENT_CASE,
                    title = "New urgent case",
                    message = "CASE-2026-0842 has been assigned to you.",
                    createdAt = now - 5 * minute,
                    read = false,
                    caseId = "CASE-2026-0842",
                ),
                AppNotification(
                    id = "n2",
                    type = NotificationType.ROUTE,
                    title = "Daily route ready",
                    message = "Your service plan for today has been updated.",
                    createdAt = now - 60 * minute,
                    read = false,
                ),
                AppNotification(
                    id = "n3",
                    type = NotificationType.INFO,
                    title = "Great work, Wubet",
                    message = "Your last case was closed successfully.",
                    createdAt = now - 26 * 60 * minute,
                    read = true,
                ),
            ),
        )
    }
}
