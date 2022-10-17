package com.electricvehicle.charge.service

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.util.ContextInitializer
import ch.qos.logback.core.joran.spi.JoranException
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory


@Context
class startupService {

	@Value("\${loki.uri}")
	private var lokiUri: String = ""

	/**
	 * Used for changing uri of loki server
	 */
	@PostConstruct
	fun onStartup() {
		val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
		val ci = ContextInitializer(loggerContext)
		val url = ci.findURLOfDefaultConfigurationFile(true)
		try {
			val configurator = JoranConfigurator()
			configurator.context = loggerContext
			loggerContext.reset()
			loggerContext.putProperty("LOKI_URI", lokiUri)
			configurator.doConfigure(url)
		} catch (je: JoranException) {

		}
	}
}