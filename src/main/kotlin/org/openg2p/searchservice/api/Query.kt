package org.openg2p.searchservice.api

import javax.validation.constraints.NotEmpty

/**
 * Beneficiary search query
 */
data class Query(

    /**
     * attributes being used to search for a beneficiary.
     * See searchservice.allowed_query_attributes for allowed values
     */
    @NotEmpty
    val attributes: Map<String, Any>,

    /**
     * allows clients to specify what backends this query should be run against. If empty or null we use all active
     */
    val backends: List<String>? = null
)