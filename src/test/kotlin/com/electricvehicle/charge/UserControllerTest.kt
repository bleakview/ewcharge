package com.electricvehicle.charge

import com.electricvehicle.charge.controller.AdminController
import com.electricvehicle.charge.model.UserModel
import com.electricvehicle.charge.model.UserNamePasswordModel
import com.electricvehicle.charge.model.UserUpsertModel
import com.electricvehicle.charge.repository.UserRepository
import com.electricvehicle.charge.service.HashIdService
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
class UserControllerTest {
	val controllerUrl = "user"

	@Inject
	@field:Client("/")
	lateinit var client: HttpClient

	@Inject
	lateinit var userRepository: UserRepository

	@Inject
	lateinit var adminController: AdminController

	@Inject
	lateinit var hashIdService: HashIdService

	private val mapper = jacksonObjectMapper()

	@Test
	fun testFindNonExistingUserReturn404() {
		adminController.createTestUsers(UserNamePasswordModel("admin", "magic"))
		val creds = UsernamePasswordCredentials("admin", "admin")
		val loginRequest: HttpRequest<*> = HttpRequest.POST("/login", creds)
		val rsp: HttpResponse<BearerAccessRefreshToken> =
			client.toBlocking().exchange(loginRequest, BearerAccessRefreshToken::class.java)
		assertEquals(HttpStatus.OK, rsp.status)

		val bearerAccessRefreshToken: BearerAccessRefreshToken = rsp.body()
		assertEquals("admin", bearerAccessRefreshToken.username)
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
	fun testUserOperations() {
		adminController.createTestUsers(UserNamePasswordModel("admin", "magic"))

		val creds = UsernamePasswordCredentials("admin", "admin")
		val loginRequest: HttpRequest<*> = HttpRequest.POST("/login", creds)
		val rsp: HttpResponse<BearerAccessRefreshToken> =
			client.toBlocking().exchange(loginRequest, BearerAccessRefreshToken::class.java)
		assertEquals(HttpStatus.OK, rsp.status)

		val bearerAccessRefreshToken: BearerAccessRefreshToken = rsp.body()
		assertEquals("admin", bearerAccessRefreshToken.username)
		assertNotNull(bearerAccessRefreshToken.accessToken)
		Assertions.assertTrue(JWTParser.parse(bearerAccessRefreshToken.accessToken) is SignedJWT)

		val accessToken: String = bearerAccessRefreshToken.accessToken

		var dbuser = userRepository.getUserByUserName("test")
		if (dbuser != null) {
			var deleteRequest = HttpRequest.DELETE(
				"/${controllerUrl}/harddelete/${hashIdService.encodeHash(dbuser.id)}", ""
			)
				.bearerAuth(accessToken)
			var deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)
		}
		dbuser = userRepository.getUserByUserName("test-test")
		if (dbuser != null) {
			var deleteRequest = HttpRequest.DELETE(
				"/${controllerUrl}/harddelete/${hashIdService.encodeHash(dbuser.id)}", ""
			)
				.bearerAuth(accessToken)
			var deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)
		}
		dbuser = userRepository.getUserByUserName("test-test-test")
		if (dbuser != null) {
			var deleteRequest = HttpRequest.DELETE(
				"/${controllerUrl}/harddelete/${hashIdService.encodeHash(dbuser.id)}", ""
			)
				.bearerAuth(accessToken)
			var deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)
		}
		var getRequest = HttpRequest.GET<List<UserModel>>("/${controllerUrl}").bearerAuth(accessToken)
		var getResponse = client.toBlocking().retrieve(getRequest, Argument.listOf(UserModel::class.java))
		val currentCount = getResponse.size

		var userUpsertModel = UserUpsertModel(
			id = "",
			isActive = true,
			name = "test",
			password = "password",
			role = "role",
		)
		var json = mapper.writeValueAsString(userUpsertModel)

		var request = HttpRequest.POST("/${controllerUrl}", json).bearerAuth(accessToken)
		var response = client.toBlocking().exchange(request, UserModel::class.java)
		var user = response.body()

		assertEquals(HttpStatus.CREATED, response.status)
		assertEquals("/${controllerUrl}/${user?.id}", response.header(HttpHeaders.LOCATION))
		userUpsertModel = UserUpsertModel(
			id = "",
			isActive = true,
			name = "test-test",
			password = "password",
			role = "role",
		)

		json = mapper.writeValueAsString(userUpsertModel)
		request = HttpRequest.POST("/${controllerUrl}", json).bearerAuth(accessToken)
		response = client.toBlocking().exchange(request, UserModel::class.java)

		user = response.body()

		assertEquals(HttpStatus.CREATED, response.status)
		assertEquals("/${controllerUrl}/${user?.id}", response.header(HttpHeaders.LOCATION))
		user = response.body()

		assertEquals("test-test", user?.name)
		userUpsertModel = UserUpsertModel(
			id = user.id,
			isActive = true,
			name = "test-test-test",
			password = "password",
			role = "role",
		)
		json = mapper.writeValueAsString(userUpsertModel)
		var cmdRequest = HttpRequest.PUT("/${controllerUrl}", json).bearerAuth(accessToken)
		response = client.toBlocking().exchange(cmdRequest)

		assertEquals(HttpStatus.NO_CONTENT, response.status())

		getRequest = HttpRequest.GET<List<UserModel>>("/${controllerUrl}/${user?.id}").bearerAuth(accessToken)
		user = client.toBlocking().retrieve(getRequest, UserModel::class.java)

		assertEquals("test-test-test", user?.name)

		var validationRequest = """			
			{
			  "userName": "test-test-test",
			  "password": "password"
			}			
		""".trimIndent()
		request = HttpRequest.POST("/${controllerUrl}/validate", validationRequest).bearerAuth(accessToken)
		var validationResponse = client.toBlocking().exchange(request, Boolean::class.java)
		assertEquals(true, validationResponse.body())

		validationRequest = """			
			{
			  "userName": "test-test-test",
			  "password": "wrongpassword"
			}			
		""".trimIndent()
		request = HttpRequest.POST("/${controllerUrl}/validate", validationRequest).bearerAuth(accessToken)
		validationResponse = client.toBlocking().exchange(request, Boolean::class.java)
		assertEquals(false, validationResponse.body())

		getRequest = HttpRequest.GET<List<UserModel>>("/${controllerUrl}/").bearerAuth(accessToken)
		getResponse = client.toBlocking().retrieve(getRequest, Argument.listOf(UserModel::class.java))

		assertEquals(currentCount + 2, getResponse.size)

		assertEquals(HttpStatus.NO_CONTENT, response.status)

		var deleteRequest = HttpRequest.DELETE("/${controllerUrl}/${user?.id}", "").bearerAuth(accessToken)
		var deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)


		assertEquals(HttpStatus.NO_CONTENT, deleteResult.status)

		getRequest = HttpRequest.GET<List<UserModel>>("/${controllerUrl}/").bearerAuth(accessToken)
		getResponse = client.toBlocking().retrieve(getRequest, Argument.listOf(UserModel::class.java))

		assertEquals(currentCount + 1, getResponse.size)

		getRequest = HttpRequest.GET<List<UserModel>>("/${controllerUrl}/list").bearerAuth(accessToken)
		getResponse = client.toBlocking().retrieve(getRequest, Argument.listOf(UserModel::class.java))

		assertEquals(currentCount + 2, getResponse.size)

		dbuser = userRepository.getUserByUserName("test-test-test")

		deleteRequest = HttpRequest.DELETE(
			"/${controllerUrl}/harddelete/${hashIdService.encodeHash(dbuser!!.id)}", ""
		)
			.bearerAuth(accessToken)
		deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)

		assertEquals(HttpStatus.NO_CONTENT, deleteResult.status)

		getRequest = HttpRequest.GET<List<UserModel>>("/${controllerUrl}/list").bearerAuth(accessToken)
		getResponse = client.toBlocking().retrieve(getRequest, Argument.listOf(UserModel::class.java))

		assertEquals(currentCount + 1, getResponse.size)
		dbuser = userRepository.getUserByUserName("test")
		if (dbuser != null) {
			var deleteRequest = HttpRequest.DELETE(
				"/${controllerUrl}/harddelete/${hashIdService.encodeHash(dbuser.id)}", ""
			)
				.bearerAuth(accessToken)
			var deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)
		}
		dbuser = userRepository.getUserByUserName("test-test")
		if (dbuser != null) {
			var deleteRequest = HttpRequest.DELETE(
				"/${controllerUrl}/harddelete/${hashIdService.encodeHash(dbuser.id)}", ""
			)
				.bearerAuth(accessToken)
			var deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)
		}
		dbuser = userRepository.getUserByUserName("test-test-test")
		if (dbuser != null) {
			var deleteRequest = HttpRequest.DELETE(
				"/${controllerUrl}/harddelete/${hashIdService.encodeHash(dbuser.id)}", ""
			)
				.bearerAuth(accessToken)
			var deleteResult = client.toBlocking().exchange(deleteRequest, String::class.java)
		}
	}
}