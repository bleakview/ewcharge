package com.electricvehicle.charge

import com.electricvehicle.charge.controller.AppClient
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest
class DeclarativeHttpClientWithJwtTest {

	@Inject
	lateinit var appClient: AppClient

	@Test
	fun verifyJwtAuthenticationWorksWithDeclarativeClient() {
		val creds: UsernamePasswordCredentials = UsernamePasswordCredentials("sherlock", "password")
		val loginRsp: BearerAccessRefreshToken = appClient.login(creds)

		assertNotNull(loginRsp)
		assertNotNull(loginRsp.accessToken)
		assertTrue(JWTParser.parse(loginRsp.accessToken) is SignedJWT)

		val msg = appClient.home("Bearer ${loginRsp.accessToken}")
		assertEquals("sherlock", msg)
	}
}