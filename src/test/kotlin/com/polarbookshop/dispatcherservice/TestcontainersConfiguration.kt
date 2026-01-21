package com.polarbookshop.dispatcherservice

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	fun rabbitMQContainer(): RabbitMQContainer =
		RabbitMQContainer(DockerImageName.parse("rabbitmq:4.2.2-management"))
}
