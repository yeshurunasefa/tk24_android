package com.example.tech24.model

fun CallEntry.toCase(): Case {
    return Case(
        caseId = callentry_id,

        serialNumber =
            atmterminal?.atmterminal_no ?: "-",

        bank =
            bank?.bank_name ?: "-",

        branch =
            branch?.branch_name ?: "-",

        district =
            district?.dist_name ?: "-",

        atmName =
            atmterminal?.atmterminal_name ?: "-",

        caseType =
            issuesubcategory?.issuesubcat_name ?: "-",

        slaStatus =
            sla_status,

        comment =
            callentry_description?.ifBlank { "-" } ?: "-",

        technician =
            assigned_eng ?: "-",

        startTime =
            created_at ?: "-",

        endTime =
            completed_at,

        status =
            when (callentry_status) {
                "Completed" -> CaseStatus.COMPLETED
                "Pending" -> CaseStatus.PENDING
                else -> CaseStatus.ONGOING
            }
    )
}