package com.example.tech24.model

data class CallEntriesResponse(
    val data: List<CallEntry>,
    val links: PaginationLinks?,
    val meta: PaginationMeta?
)

data class PaginationLinks(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

data class PaginationMeta(
    val current_page: Int,
    val from: Int?,
    val last_page: Int,
    val per_page: Int,
    val to: Int?,
    val total: Int
)
data class CallEntry(
    val callentry_id: Int,
    val callentry_description: String?,
    val callentry_status: String?,
    val callentry_progress: String?,
    val callentry_active: Boolean,
    val approval_status: String?,
    val reopen_count: Int,

    val region: Region?,
    val bank: Bank?,
    val branch: Branch?,
    val district: District?,
    val subdistrict: Subdistrict?,
    val atmterminal: AtmTerminal?,
    val issuecategory: IssueCategory?,
    val issuesubcategory: IssueSubcategory?,

    val sla_status: Int,
    val callassigned: Boolean,
    val assigned_eng: String?,
    val assigned_eng_phonenumber: String?,
    val assigned_at: String?,
    val completed_at: String?,
    val created_at: String?,
    val updated_at: String?
)

data class Region(
    val region_id: Int,
    val region_name: String
)

data class Bank(
    val bank_id: Int,
    val bank_name: String
)

data class Branch(
    val branch_id: Int,
    val branch_name: String
)

data class District(
    val dist_id: Int,
    val dist_name: String
)

data class Subdistrict(
    val subdist_id: Int,
    val subdist_name: String
)

data class AtmTerminal(
    val atmterminal_id: Int,
    val atmterminal_name: String,
    val atmterminal_no: String
)

data class IssueCategory(
    val issuecat_id: Int,
    val issuecat_name: String
)

data class IssueSubcategory(
    val issuesubcat_id: Int,
    val issuesubcat_name: String
)