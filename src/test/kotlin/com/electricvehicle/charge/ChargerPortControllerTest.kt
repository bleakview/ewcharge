package com.electricvehicle.charge

import com.electricvehicle.charge.controller.AdminController
import com.electricvehicle.charge.model.ChargerPortModel
import com.electricvehicle.charge.model.ChargerPortUpsertModel
import com.electricvehicle.charge.model.UserNamePasswordModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest
class ChargerPortControllerTest {
	val controllerUrl = "chargerport"

	@Inject
	@field:Client("/")
	lateinit var client: HttpClient

	@Inject
	lateinit var adminController: AdminController

	private val mapper = jacksonObjectMapper()

	@Test
	fun testFindNonExistingChargePortReturn404() {
		adminController.createTestUsers(UserNamePasswordModel("admin", "magic"))
		val creds = UsernamePasswordCredentials("sherlock", "password")
		val loginRequest: HttpRequest<*> = HttpRequest.POST("/login", creds)
		val rsp: HttpResponse<BearerAccessRefreshToken> =
			client.toBlocking().exchange(loginRequest, BearerAccessRefreshToken::class.java)
		assertEquals(HttpStatus.OK, rsp.status)

		val bearerAccessRefreshToken: BearerAccessRefreshToken = rsp.body()
		assertEquals("sherlock", bearerAccessRefreshToken.username)
		assertNotNull(bearerAccessRefreshToken.accessToken)
		Assertions.assertTrue(JWTParser.parse(bearerAccessRefreshToken.accessToken) is SignedJWT)
		val accessToken: String = bearerAccessRefreshToken.accessToken
		var request = HttpRequest.GET<String>("/${controllerUrl}/99").bearerAuth(accessToken)
		val thrown = assertThrows<HttpClientResponseException> {
			client.toBlocking().exchange(request, String::class.java)
		}

		assertNotNull(thrown.response)
		assertEquals(HttpStatus.NOT_FOUND, thrown.status)
	}

	@Test
	fun testChargePortOperations() {
		adminController.createTestUsers(UserNamePasswordModel("admin", "magic"))
		val creds = UsernamePasswordCredentials("sherlock", "password")
		val loginRequest: HttpRequest<*> = HttpRequest.POST("/login", creds)
		val rsp: HttpResponse<BearerAccessRefreshToken> =
			client.toBlocking().exchange(loginRequest, BearerAccessRefreshToken::class.java)
		assertEquals(HttpStatus.OK, rsp.status)

		val bearerAccessRefreshToken: BearerAccessRefreshToken = rsp.body()
		assertEquals("sherlock", bearerAccessRefreshToken.username)
		assertNotNull(bearerAccessRefreshToken.accessToken)
		Assertions.assertTrue(JWTParser.parse(bearerAccessRefreshToken.accessToken) is SignedJWT)

		val accessToken: String = bearerAccessRefreshToken.accessToken

		var getRequest = HttpRequest.GET<List<ChargerPortModel>>("/${controllerUrl}/").bearerAuth(accessToken)
		var getResponse = client.toBlocking().retrieve(getRequest, Argument.listOf(ChargerPortModel::class.java))
		var currentCount = getResponse.size

		var chargerPortUpsertModel = ChargerPortUpsertModel(
			id = "",
			isActive = true,
			name = "test",
			latitude = 0.0,
			longitude = 0.0,
			powerInWatt = 1,
			chargerPlugTypeName = "plug",
		)
		var json = mapper.writeValueAsString(chargerPortUpsertModel)

		var request = HttpRequest.POST("/${controllerUrl}", json).bearerAuth(accessToken)
		var response = client.toBlocking().exchange(request, ChargerPortModel::class.java)
		var chargePort = response.body()

		assertEquals(HttpStatus.CREATED, response.status)
		assertEquals("/${controllerUrl}/${chargePort.id}", response.header(HttpHeaders.LOCATION))
		chargerPortUpsertModel = ChargerPortUpsertModel(
			id = "",
			isActive = true,
			name = "test",
			latitude = 0.0,
			longitude = 0.0,
			powerInWatt = 1,
			chargerPlugTypeName = "plug",
		)

		json = mapper.writeValueAsString(chargerPortUpsertModel)
		request = HttpRequest.POST("/${controllerUrl}/", json).bearerAuth(accessToken)
		response = client.toBlocking().exchange(request, ChargerPortModel::class.java)

		chargePort = response.body()
		assertEquals(HttpStatus.CREATED, response.status)
		assertEquals("/${controllerUrl}/${chargePort.id}", response.header(HttpHeaders.LOCATION))
		chargePort = response.body()

		assertEquals("test", chargePort.name)
		chargerPortUpsertModel = ChargerPortUpsertModel(
			id = chargePort.id,
			isActive = true,
			name = "test-test",
			latitude = 0.0,
			longitude = 0.0,
			powerInWatt = 1,
			chargerPlugTypeName = "plug",
		)
		json = mapper.writeValueAsString(chargerPortUpsertModel)
		var cmdRequest = HttpRequest.PUT("/${controllerUrl}", json).bearerAuth(accessToken)
		response = client.toBlocking().exchange(cmdRequest)

		assertEquals(HttpStatus.NO_CONTENT, response.status())

		getRequest =
			HttpRequest.GET<List<ChargerPortModel>>("/${controllerUrl}/${chargePort.id}").bearerAuth(accessToken)
		chargePort = client.toBlocking().retrieve(getRequest, ChargerPortModel::class.java)

		assertEquals("test-test", chargePort.name)

		getRequest = HttpRequest.GET<List<ChargerPortModel>>("/${controllerUrl}/").bearerAuth(accessToken)
		getResponse = client.toBlocking().retrieve(getRequest, Argument.listOf(ChargerPortModel::class.java))

		assertEquals(currentCount + 2, getResponse.size)

		assertEquals(HttpStatus.NO_CONTENT, response.status)

		var deleteRequest = HttpRequest.DELETE("/${controllerUrl}/${chargePort.id}", "").bearerAuth(accessToken)
		var deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)


		assertEquals(HttpStatus.NO_CONTENT, deleteResult.status)

		getRequest = HttpRequest.GET<List<ChargerPortModel>>("/${controllerUrl}/").bearerAuth(accessToken)
		getResponse = client.toBlocking().retrieve(getRequest, Argument.listOf(ChargerPortModel::class.java))

		assertEquals(currentCount + 1, getResponse.size)
	}

}