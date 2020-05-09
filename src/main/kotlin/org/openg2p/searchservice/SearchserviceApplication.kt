package org.openg2p.searchservice

import org.openg2p.searchservice.config.Configurations
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableConfigurationProperties(Configurations::class)
@EnableWebFlux
@Configuration
class SearchserviceApplication

fun main(args: Array<String>) {
	runApplication<SearchserviceApplication>(*args)
}
