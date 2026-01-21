package com.polarbookshop.dispatcherservice

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.TimeUnit

@Testcontainers
@Import(
	TestcontainersConfiguration::class,
	RabbitMQConfiguration::class,
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DispatcherServiceApplicationTests @Autowired constructor(
	private val rabbitTemplate: RabbitTemplate,
	private val amqpAdmin: AmqpAdmin,
) {

	companion object {
		@Container
		@JvmStatic
		val rabbitMQContainer = RabbitMQContainer(
			DockerImageName.parse("rabbitmq:4.2.2-management")
		).apply { start() }

		@JvmStatic
		@DynamicPropertySource
		fun rabbitMQProperties(registry: DynamicPropertyRegistry) {
			registry.apply {
				add("spring.rabbitmq.host", rabbitMQContainer::getHost)
				add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort)
				add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername)
				add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword)
			}
		}
	}

	@Test
	fun contextLoads() {
	}

	@Test
	fun packAndLabel() {
		// GIVEN
		val orderId = 123L

		val inputQueue = amqpAdmin.declareQueue()
		check(inputQueue != null)
		val inputBinding = Binding(inputQueue.name, Binding.DestinationType.QUEUE, "order-accepted", "dispatcher-service", null)

		val outputQueue = amqpAdmin.declareQueue()
		check(outputQueue != null)
		val outputBinding = Binding(outputQueue.name, Binding.DestinationType.QUEUE, "order-dispatched", "#", null)

		amqpAdmin.declareBinding(inputBinding)
		amqpAdmin.declareBinding(outputBinding)

		// WHEN
		rabbitTemplate.convertAndSend("order-accepted", "dispatcher-service", OrderAcceptedMessage(orderId))

		// THEN
		await.atMost(5, TimeUnit.SECONDS).untilAsserted {
			val orderDispatchedMessage = rabbitTemplate.receiveAndConvert(
				outputQueue.name,
				10000L,
				object : ParameterizedTypeReference<OrderDispatchedMessage>() {},
			)

			orderDispatchedMessage shouldNotBe null
			orderDispatchedMessage!!.id shouldBe orderId
		}
	}
}
