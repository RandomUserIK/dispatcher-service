package com.polarbookshop.dispatcherservice

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.function.context.FunctionCatalog
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.function.Function

@FunctionalSpringBootTest
@Suppress("ReactiveStreamsUnusedPublisher")
internal class DispatchingFunctionsIntegrationTests @Autowired constructor(
	private val catalog: FunctionCatalog,
) {

	@Test
	fun pack() {
		val pack = catalog.lookup<Function<OrderAcceptedMessage, Long>>(
			Function::class.java,
			"pack",
		)
		val orderId = 666L

		pack.apply(
			OrderAcceptedMessage(orderId)
		) shouldBe orderId
	}

	@Test
	fun label() {
		val label = catalog.lookup<Function<Flux<Long>, Flux<OrderDispatchedMessage>>>(
			Function::class.java,
			"label",
		)
		val orderId = 666L
		val orderFlux = Flux.just(orderId)

		StepVerifier
			.create(
				label.apply(orderFlux)
			)
			.expectNextMatches { it.id == orderId }
			.verifyComplete()
	}

	@Test
	fun packAndLabelOrder() {
		val packAndLabel = catalog.lookup<Function<OrderAcceptedMessage, Flux<OrderDispatchedMessage>>>(
			Function::class.java,
			"pack|label",
		)
		val orderId = 666L

		StepVerifier
			.create(
				packAndLabel.apply(OrderAcceptedMessage(orderId))
			)
			.expectNextMatches { it == OrderDispatchedMessage(it.id) }
			.verifyComplete()
	}
}
