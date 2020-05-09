package org.openg2p.searchservice.api

import javax.validation.constraints.NotEmpty

/**
 * Beneficiary search query response
 */
data class Response(

    val beneficiary: String,

    /**
     *  reason for matching beneficiary
     */
    val reasons: List<String>
)