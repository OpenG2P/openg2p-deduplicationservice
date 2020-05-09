package org.openg2p.searchservice.services

import org.openg2p.searchservice.api.Query
import org.openg2p.searchservice.api.Response
import reactor.core.publisher.Mono

/**
 * Pretty much calls on backends
 */
interface SearchEngine {

    /**
     * Get a list of active backends
     */
    fun backends(): List<String>

    /**
     * Calls our backends to index a payload
     */
    fun index(id: String, payload: Map<String, Any>): Mono<Boolean>

    /**
     * Requests that backends re-indexes all the data it holds
     */
    fun reIndexAll(): Mono<Boolean>

    /**
     * Has active backends conduct a search and returns a list of IDs of beneficiaries matching this query
     * @return a map of matched beneficiary id along with a list explaining why it was matched
     */
    fun search(query: Query): Mono<List<Response>>

    /**
     * Remove from the backends
     */
    fun remove(id: String): Mono<Boolean>

}
