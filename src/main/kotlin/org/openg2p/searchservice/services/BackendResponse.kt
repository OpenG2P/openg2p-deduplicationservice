package org.openg2p.searchservice.services

/**
 * Response from backend indicating each match
 */
data class BackendResponse(
    /**
     * ID of the beneficiary. Same as the ID it was indexed under
     */
    val beneficiary: String,

    val score: Double,

    /**
     * indicates the reasons for the match
     */
    val explanation: List<String>
)