package com.polarbookshop.dispatcherservice

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration(proxyBeanMethods = false)
class RabbitMQConfiguration {

	@Bean
	fun messageConverter() = Jackson2JsonMessageConverter()
}
