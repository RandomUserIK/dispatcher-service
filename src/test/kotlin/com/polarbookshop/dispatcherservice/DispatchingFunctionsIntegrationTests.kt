package com.polarbookshop.dispatcherservice

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.function.context.FunctionCatalog
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.function.Function

@FunctionalSpringBootTest
internal class DispatchingFunctionsIntegrationTests @Autowired constructor(
	private val catalog: FunctionCatalog,
) {

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
