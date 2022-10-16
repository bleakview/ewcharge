package com.electricvehicle.charge.service

import com.electricvehicle.charge.repository.UserRepository
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

/**
 * This service is for Authentication,Authorization with JWT tokens
 */
@Singleton
class AuthenticationProviderUserPassword(private val userRepository: UserRepository) : AuthenticationProvider {
	private val log: Logger = LoggerFactory.getLogger(AuthenticationProviderUserPassword::class.java)

	/**
	 * Authenticate users with username password
	 */
	override fun authenticate(
		httpRequest: HttpRequest<*>?,
		authenticationRequest: AuthenticationRequest<*, *>
	): Publisher<AuthenticationResponse> {
		return Flux.create({ emitter: FluxSink<AuthenticationResponse> ->
			val user = userRepository.validateUser(
				authenticationRequest.identity.toString(),
				authenticationRequest.secret.toString()
			)
			if (user != null) {
				emitter.next(AuthenticationResponse.success(user.userName, listOf(user.role)))
				emitter.complete()
			} else {
				emitter.error(AuthenticationResponse.exception())
			}
		}, FluxSink.OverflowStrategy.ERROR)
	}
}