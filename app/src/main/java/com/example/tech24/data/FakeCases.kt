package com.example.tech24.data

import com.example.tech24.model.Case
import com.example.tech24.model.CaseStatus

val fakeCases = listOf(

    Case(
        caseId = 16327,
        serialNumber = "PAGRL690",
        bank = "Dashen",
        branch = "Robik Hote",
        district = "South A.A.",
        atmName = "Robik Hote ATM 1",
        caseType = "Dispenser problem",
        slaStatus = 1,
        comment = "-",
        technician = "Yeshurun Asefa",
        startTime = "27/07/2026 11:11 AM",
        endTime = null,
        status = CaseStatus.ONGOING
    ),

    Case(
        caseId = 16284,
        serialNumber = "ATM9912",
        bank = "CBE",
        branch = "Bole",
        district = "Addis Ababa",
        atmName = "Bole ATM",
        caseType = "Cash Jam",
        slaStatus = 2,
        comment = "Resolved successfully.",
        technician = "Yeshurun Asefa",
        startTime = "26/07/2026 08:30 AM",
        endTime = "26/07/2026 09:12 AM",
        status = CaseStatus.COMPLETED
    ),

    Case(
        caseId = 16260,
        serialNumber = "ATM6677",
        bank = "Awash",
        branch = "Piassa",
        district = "Addis Ababa",
        atmName = "Piassa ATM",
        caseType = "Printer Error",
        slaStatus = 1,
        comment = "Waiting for spare part.",
        technician = "Yeshurun Asefa",
        startTime = "26/07/2026 01:45 PM",
        endTime = null,
        status = CaseStatus.PENDING
    ),

    Case(
        caseId = 16245,
        serialNumber = "ATM3355",
        bank = "Abyssinia",
        branch = "Mexico",
        district = "Addis Ababa",
        atmName = "Mexico Branch ATM",
        caseType = "Card Reader Failure",
        slaStatus = 1,
        comment = "-",
        technician = "Yeshurun Asefa",
        startTime = "25/07/2026 10:00 AM",
        endTime = null,
        status = CaseStatus.ONGOING
    )
)