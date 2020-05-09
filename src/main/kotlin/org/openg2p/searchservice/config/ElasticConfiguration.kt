package org.openg2p.searchservice.config

import org.apache.http.HttpHost
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class ElasticConfiguration: AbstractReactiveElasticsearchConfiguration() {

    @Value("\${searchservice.elastic.endpoint}")  lateinit var elasticEndpoint: String

    @Bean
    fun client(): RestHighLevelClient? {
        val host = HttpHost.create(elasticEndpoint)
        val clientConfiguration = ClientConfiguration.builder()
            .connectedTo("${host.hostName}:${host.port}")
            .build()
        return RestClients.create(clientConfiguration).rest()
    }

    @Bean
    override fun reactiveElasticsearchClient(): ReactiveElasticsearchClient {
        val host = HttpHost.create(elasticEndpoint)
        val clientConfiguration = ClientConfiguration.builder()
            .connectedTo("${host.hostName}:${host.port}")
            .withWebClientConfigurer { webClient: WebClient ->
                val exchangeStrategies = ExchangeStrategies.builder()
                    .codecs { configurer: ClientCodecConfigurer ->
                        configurer.defaultCodecs()
                            .maxInMemorySize(-1)
                    }
                    .build()
                webClient.mutate().exchangeStrategies(exchangeStrategies).build()
            }
            .build()
        return ReactiveRestClients.create(clientConfiguration)
    }
}