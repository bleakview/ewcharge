package com.electricvehicle.charge.controller

import com.electricvehicle.charge.repository.RefreshTokenRepository
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.errors.IssuingAnAccessTokenErrorCode.INVALID_GRANT
import io.micronaut.security.errors.OauthErrorResponseException
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent
import io.micronaut.security.token.refresh.RefreshTokenPersistence
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

@Singleton
class CustomRefreshTokenPersistence(private val refreshTokenRepository: RefreshTokenRepository) :
	RefreshTokenPersistence {

	override fun persistToken(event: RefreshTokenGeneratedEvent?) {
		if (event?.refreshToken != null && event.authentication?.name != null) {
			val payload = event.refreshToken
			refreshTokenRepository.save(event.authentication.name, payload, false)
		}
	}

	override fun getAuthentication(refreshToken: String): Publisher<Authentication> {
		return Flux.create({ emitter: FluxSink<Authentication> ->
			val tokenOpt = refreshTokenRepository.findByRefreshToken(refreshToken)
			if (tokenOpt.isPresent) {
				val (_, username, _, revoked, role) = tokenOpt.get()
				if (revoked) {
					emitter.error(OauthErrorResponseException(INVALID_GRANT, "refresh token revoked", null))
				} else {
					val roles = mutableListOf<String>()
					roles.add(role)
					emitter.next(Authentication.build(username, roles))
					emitter.complete()
				}
			} else {
				emitter.error(OauthErrorResponseException(INVALID_GRANT, "refresh token not found", null))
			}
		}, FluxSink.OverflowStrategy.ERROR)
	}
}