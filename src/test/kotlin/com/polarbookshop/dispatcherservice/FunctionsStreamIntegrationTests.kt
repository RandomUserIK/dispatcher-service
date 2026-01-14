package com.polarbookshop.dispatcherservice

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.integration.support.MessageBuilder

@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
internal class FunctionsStreamIntegrationTests @Autowired constructor(
	private val input: InputDestination,
	private val output: OutputDestination,
	private val objectMapper: ObjectMapper,
) {

	@Test
	fun whenOrderAcceptedThenDispatched() {
		val orderId = 666L
		val inputMessage = MessageBuilder.withPayload(OrderAcceptedMessage(orderId)).build()
		val expectedMessage = MessageBuilder.withPayload(OrderDispatchedMessage(orderId)).build()

		input.send(inputMessage)
		val result = objectMapper.readValue(output.receive().payload, OrderDispatchedMessage::class.java)

		result shouldBe expectedMessage.payload
	}
}
