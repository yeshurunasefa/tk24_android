package com.example.tech24.model
data class Case(
    val caseId: Int,
    val serialNumber: String,
    val bank: String,
    val branch: String,
    val district: String,
    val atmName: String,
    val caseType: String,
    val slaStatus: Int,
    val comment: String,
    val technician: String,
    val startTime: String,
    val endTime: String?,
    val status: CaseStatus
)