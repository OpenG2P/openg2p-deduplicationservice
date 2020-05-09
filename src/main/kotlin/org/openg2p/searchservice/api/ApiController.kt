package org.openg2p.searchservice.api

import org.openg2p.searchservice.exceptions.ValidationException
import org.openg2p.searchservice.services.SearchEngine
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import javax.validation.Valid

@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
@RestController
class ApiController constructor(private val searchEngine: SearchEngine) {

    /**
     * Endpoint to ensure bioanalyzer is up and running.
     * @return
     */
    @GetMapping("/health")
    fun healthCheck(): Mono<ResponseEntity<String>> {
        return ResponseEntity.ok("OK").toMono()
    }

    @GetMapping("/backends")
    fun backends(payload: Map<String, Any>): Mono<ResponseEntity<List<String>>> {
        return ResponseEntity.ok(searchEngine.backends()).toMono()
    }

    @PostMapping("/index")
    fun index(@RequestBody payload: Map<String, Any>): Mono<ResponseEntity<Boolean>> {
        if ("id" !in payload)
            return Mono.error(ValidationException("Index payload requires and 'id' attribute"))
        return searchEngine.index(payload["id"]!! as String, payload)
            .map { ResponseEntity.ok(it) }
    }

    @DeleteMapping("/index/{id}")
    fun delete(@PathVariable id: String): Mono<ResponseEntity<Boolean>> {
        return searchEngine.remove(id)
            .map { ResponseEntity.ok(it) }
    }

    /**
     * Search for beneficiary
     */
    @PostMapping("/index/search", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun search(@Valid @RequestBody query: Query): Mono<ResponseEntity<List<Response>>> {
        return  searchEngine.search(query).flatMap { ResponseEntity.ok(it).toMono() }
    }
}
