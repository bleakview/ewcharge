package com.electricvehicle.charge.controller

import io.micronaut.http.HttpHeaders.AUTHORIZATION
import io.micronaut.http.MediaType.TEXT_PLAIN
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.rules.SecurityRule
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken

@Client("/")
interface AppClient {
	/**
	 * default login controller used for jwt
	 */
	@Post("/login")
	@Secured(SecurityRule.IS_ANONYMOUS)
	fun login(@Body credentials: UsernamePasswordCredentials): BearerAccessRefreshToken

	/**
	 * testing authorization token
	 */
	@Consumes(TEXT_PLAIN)
	@Get
	fun home(@Header(AUTHORIZATION) authorization: String): String
}