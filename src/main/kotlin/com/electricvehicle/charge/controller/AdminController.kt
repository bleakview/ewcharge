package com.electricvehicle.charge.controller

import com.electricvehicle.charge.entity.UserEntity
import com.electricvehicle.charge.helper.CommonRoles
import com.electricvehicle.charge.model.UserNamePasswordModel
import com.electricvehicle.charge.repository.UserRepository
import com.electricvehicle.charge.service.CacheService
import com.electricvehicle.charge.service.DatabaseService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ExecuteOn(TaskExecutors.IO)
@Controller("/admin")
@Secured(SecurityRule.IS_AUTHENTICATED)
open class AdminController(
	private val userRepository: UserRepository,
	private val cacheService: CacheService,
	private val databaseService: DatabaseService
) {
	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	@Post("/createschema")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_ANONYMOUS)
	fun createSchema(@Body userNamePasswordModel: UserNamePasswordModel): HttpResponse<String> {
		try {
			if ((userNamePasswordModel.userName == "admin") && (userNamePasswordModel.password == "magic")) {
				databaseService.createSchema()
			}
			return HttpResponse.noContent()
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}

	@Post("/deleteSchema")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_ANONYMOUS)
	fun deleteSchema(@Body userNamePasswordModel: UserNamePasswordModel): HttpResponse<String> {
		try {
			if ((userNamePasswordModel.userName == "admin") && (userNamePasswordModel.password == "magic")) {
				databaseService.dropSchema()
				cacheService.delete("UserModel_all")
				cacheService.delete("ChargerPlugTypeModel_all")
				return HttpResponse.noContent()
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}

	@Post("/createtestusers")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_ANONYMOUS)
	fun createTestUsers(@Body userNamePasswordModel: UserNamePasswordModel): HttpResponse<String> {
		try {
			if ((userNamePasswordModel.userName == "admin") && (userNamePasswordModel.password == "magic")) {
				databaseService.createSchema()
				var user = userRepository.getUserByUserName("sherlock")
				if (user != null) {
					userRepository.delete(user.id)
				}
				user = userRepository.getUserByUserName("admin")
				if (user != null) {
					userRepository.delete(user.id)
				}
				userRepository.addUser(
					UserEntity(
						id = 0,
						isActive = true,
						userName = "sherlock",
						password = "password",
						role = CommonRoles.View
					)
				)
				userRepository.addUser(
					UserEntity(
						id = 0,
						isActive = true,
						userName = "admin",
						password = "admin",
						role = CommonRoles.Admin
					)
				)
			}
			return HttpResponse.noContent()
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}
}