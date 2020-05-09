package org.openg2p.searchservice.services.backends

import org.openg2p.searchservice.api.Query
import org.openg2p.searchservice.services.BackendResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Backend called to search for a beneficiary
 */
interface Backend {

    /**
     * slug-like name of the backend. Caller can filter the list of backends that get called by specifying the
     * name in Query
     * @see org.openg2p.searchservice.api.Query
     */
    val  name: String

    /**
     * Indexes a payload under the given id
     */
    fun index(id: String, payload: Map<String, Any>): Mono<Boolean>

    /**
     * Remove from index
     */
    fun remove(id: String): Mono<Boolean>

    /**
     * Requests that the backend re-indexes all the data it holds
     */
    fun reIndexAll(): Mono<Boolean>

    /**
     * Returns a list of IDd of beneficiaries matching this query
     */
    fun search(query: Query): Flux<BackendResponse>
}
