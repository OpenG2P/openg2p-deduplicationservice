package org.openg2p.searchservice.services.backends

import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.openg2p.searchservice.api.Query
import org.openg2p.searchservice.config.Configurations
import org.openg2p.searchservice.services.BackendResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import org.springframework.util.StreamUtils
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.nio.charset.Charset
import java.nio.file.Files


@Service
class ElasticSearchBackend(
    private val client: RestHighLevelClient,
    private val configurations: Configurations,
    @Value("classpath:/elastic/index_settings.json")
    private val indexFile: Resource,
    @Value("classpath:/elastic/model_beneficiary.json")
    private val modelFile: Resource
) : Backend {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val httpClient = WebClient.builder()
        .baseUrl(configurations.elastic.endpoint)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()
    override val name: String = "elastic"





    init {
        checkZentityStatus()
        createIndexAndModelIfNotExist()
        refreshZentityModel()
    }

    override fun index(id: String, payload: Map<String, Any>): Mono<Boolean> {
        return httpClient
            .post()
            .uri("/${configurations.elastic.index}/_doc/$id")
            .body(BodyInserters.fromValue(payload))
            .exchange()
            .map {
                if (!it.statusCode().is2xxSuccessful) throw Exception("Error indexing document")
                else true
            }
    }
    override fun remove(id: String): Mono<Boolean> {
        return httpClient
            .delete()
            .uri("/${configurations.elastic.index}/_doc/$id")
            .exchange()
            .map {
                if (!it.statusCode().is2xxSuccessful) throw Exception("Error removing document")
                else true
            }
    }

    override fun search(query: Query): Flux<BackendResponse> {
        val request = mapOf(
            "attributes" to query.attributes
                .filter { it.key in configurations.elastic.attributes }
                .map {
                    if (it.value is List<*>)  Pair(it.key, it.value as List<String>)
                    else Pair(it.key, listOf(it.value as String))
                }.toMap()
        )
        return httpClient
            .post()
            .uri("/_zentity/resolution/${configurations.elastic.index}?&_source=false&_explanation=true&_score=true")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .flatMap { it.bodyToMono(Map::class.java) }
            .flatMapMany { res ->
                res?.get("hits")?.let {
                    it as Map<String, Any>
                    it["hits"] as List<Map<String, Any>>
                }?.toFlux()
            }
            .map { hit ->
                val explanation = hit["_explanation"]!!.let {
                    it as Map<String, Any>
                    val matches = it["matches"] as List<Map<String, String>>
                    matches.map {
                        "${it["target_field"]} ->  Input: ${it["input_value"]} | Match: ${it["target_value"]} | Type: ${it["input_matcher"]} "
                    }
                }
                BackendResponse(hit["_id"]!! as String, hit["_score"]!! as Double, explanation)
            }
    }

    private fun checkZentityStatus() {
        httpClient.get()
            .uri("/_zentity")
            .exchange()
            .doOnNext {
                if (!it.statusCode().is2xxSuccessful) throw Exception("Can't connect to zentity endpoint")
                logger.info("Connected to zentity endpoint")

            }
            .block()
    }

    private fun createIndexAndModelIfNotExist() {
        if (!client.indices().exists(GetIndexRequest(configurations.elastic.index), RequestOptions.DEFAULT)) {
            logger.info("Beneficiary index does not exist so creating")

            val indexSettings = StreamUtils.copyToString(indexFile.inputStream, Charset.defaultCharset())
            val createIndexResponse = client.indices().create(
                CreateIndexRequest(configurations.elastic.index).apply { source(indexSettings, XContentType.JSON) },
                RequestOptions.DEFAULT
            )
            if (!createIndexResponse.isAcknowledged or !createIndexResponse.isShardsAcknowledged) {
                throw Exception("Failed to create beneficiaries index")
            }
        }
    }

    private fun refreshZentityModel() {
        logger.info("loading zentity model into elastic")
        val model = StreamUtils.copyToString(modelFile.inputStream, Charset.defaultCharset())
        httpClient
            .put()
            .uri("/_zentity/models/${configurations.elastic.index}")
            .bodyValue(model)
            .exchange()
            .map {
                if (!it.statusCode().is2xxSuccessful) throw Exception("Error loading mapping")
                else true
            }.block()
    }

    override fun reIndexAll(): Mono<Boolean> {
        TODO("Not yet implemented")
    }

}