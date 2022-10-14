package com.electricvehicle.charge

import com.electricvehicle.charge.model.UserNamePasswordModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest
class AdminControllerTest {
	val controllerUrl = "admin"

	@Inject
	@field:Client("/")
	lateinit var client: HttpClient

	private val mapper = jacksonObjectMapper()

	@Test
	fun testAdminController() {
		var login = mapper.writeValueAsString(UserNamePasswordModel("admin", "magic"))
		var request = HttpRequest.POST("/${controllerUrl}/deleteSchema", login)
		var response = client.toBlocking().exchange(request, String::class.java)
		assertEquals(HttpStatus.NO_CONTENT, response.status)

		request = HttpRequest.POST("/${controllerUrl}/createschema", login)
		response = client.toBlocking().exchange(request, String::class.java)
		assertEquals(HttpStatus.NO_CONTENT, response.status)

		request = HttpRequest.POST("/${controllerUrl}/createtestusers", login)
		response = client.toBlocking().exchange(request, String::class.java)
		assertEquals(HttpStatus.NO_CONTENT, response.status)


	}

}