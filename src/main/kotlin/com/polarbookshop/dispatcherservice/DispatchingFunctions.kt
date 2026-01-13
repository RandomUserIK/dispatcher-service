package com.polarbookshop.dispatcherservice

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux

@Configuration
class DispatchingFunctions {
	private val logger = KotlinLogging.logger { }

	@Bean
	fun pack(): (OrderAcceptedMessage) -> Long =
		{ orderAcceptedMessage ->
			logger.info { "The order with id: ${orderAcceptedMessage.id} is packed." }
			orderAcceptedMessage.id
		}

	@Bean
	fun label(): (Flux<Long>) -> Flux<OrderDispatchedMessage> =
		{ orderFlux ->
			orderFlux.map { orderId ->
				logger.info { "The order with id: $orderId is labeled." }
				OrderDispatchedMessage(orderId)
			}
		}
}
