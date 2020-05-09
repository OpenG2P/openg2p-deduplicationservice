package org.openg2p.searchservice.services

import org.openg2p.searchservice.api.Query
import org.openg2p.searchservice.api.Response
import org.openg2p.searchservice.config.Configurations
import org.openg2p.searchservice.exceptions.ValidationException
import org.openg2p.searchservice.services.backends.Backend
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
class SearchEngineImpl(
    private val backends: List<Backend>,
    private val configurations: Configurations
) : SearchEngine {

    private val logger = LoggerFactory.getLogger(javaClass)
    init {
        logger.info("Loading ${backends.count()} backends")
    }

    override fun backends(): List<String> = backends.map { it.name }

    override fun index(id: String, payload: Map<String, Any>) =
        backends.toFlux()
            .parallel()
            .flatMap { it.index(id, payload) }
            .sequential()
            .then(true.toMono())

    override fun reIndexAll(): Mono<Boolean> {
        TODO("Not yet implemented")
    }

    override fun search(query: Query): Mono<List<Response>> {
        val notSupported = query.attributes.keys - configurations.allowedQueryAttributes.toSet()
        if (notSupported.isNotEmpty())
            throw ValidationException("Query unsupported attributes ${notSupported.joinToString(", ")}")

        val candidateBackend = if (query.backends.isNullOrEmpty()) backends
        else backends.filter { it.name in query.backends }
        return candidateBackend.toFlux()
            .parallel()
            .flatMap { it.search(query) }
            .sequential()
            .collectList()
            .doOnNext { logger.debug("Match Result -  $it") }
            .map { response ->
                val res: MutableMap<String, MutableList<String>> = mutableMapOf()
                response.forEach {
                    if (it.beneficiary in res) res[it.beneficiary]!!.addAll(it.explanation)
                    else res[it.beneficiary] = it.explanation.toMutableList()
                }
                res.map { Response(it.key, it.value) }
            }
    }

    override fun remove(id: String): Mono<Boolean> {
       return backends.toFlux()
            .parallel()
            .flatMap { it.remove(id) }
            .sequential()
            .then(true.toMono())
    }
}
