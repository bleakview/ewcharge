package com.electricvehicle.charge.controller

import com.electricvehicle.charge.entity.UserEntity
import com.electricvehicle.charge.entity.toUserModel
import com.electricvehicle.charge.helper.CommonRoles
import com.electricvehicle.charge.model.UserModel
import com.electricvehicle.charge.model.UserNamePasswordModel
import com.electricvehicle.charge.model.UserUpsertModel
import com.electricvehicle.charge.model.toRecord
import com.electricvehicle.charge.repository.UserRepository
import com.electricvehicle.charge.service.CacheService
import com.electricvehicle.charge.service.HashIdService
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.security.RolesAllowed


@ExecuteOn(TaskExecutors.IO)
@Controller("/user")
@Secured(SecurityRule.IS_AUTHENTICATED)
open class UserController(
	private val userRepository: UserRepository,
	private val cacheService: CacheService,
	private val hashIdService: HashIdService,
) {
	private val cachePrefix = "UserModel"
	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	@Get("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun get(id: String): UserModel? {
		var userModel = cacheService.getData("${cachePrefix}_$id", UserModel::class.java)
		try {
			if (userModel == null) {
				userModel = userRepository.find(id)
					?.toUserModel(hashIdService)
				if (userModel != null) {
					cacheService.putData("${cachePrefix}_$id", userModel)
				}
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return userModel
	}

	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun getallActive(): List<UserModel>? {
		var userModel = cacheService.getData("${cachePrefix}_active_all")
				as? List<UserModel>
		try {
			if (userModel == null) {
				val userEntity = userRepository.getAllActive()?.filterIsInstance<UserEntity>()
				userModel = userEntity?.map { t -> t.toUserModel(hashIdService) }
				if (userModel != null) {
					cacheService.putData("${cachePrefix}_active_all", userModel)
				}
			}

		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return userModel
	}

	@Get("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun getall(): List<UserModel>? {
		var userModel = cacheService.getData("${cachePrefix}_all")
				as? List<UserModel>
		try {
			if (userModel == null) {
				val userEntity = userRepository.getAll()?.filterIsInstance<UserEntity>()
				userModel = userEntity?.map { t -> t.toUserModel(hashIdService) }
				if (userModel != null) {
					cacheService.putData("${cachePrefix}_all", userModel)
				}
			}

		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return userModel
	}

	@Post("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun post(@Body userUpsertModel: UserUpsertModel): HttpResponse<UserModel?> {
		try {
			val insertModel = userUpsertModel.toRecord(hashIdService)
			val userValue = userRepository.addUser(insertModel)
			cacheService.delete("${cachePrefix}_all")
			cacheService.delete("${cachePrefix}_active_all")
			val result = userValue?.toUserModel(hashIdService)
			return HttpResponse.created(result).header(HttpHeaders.LOCATION, "/user/${result?.id}")
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}

	@Post("/validate")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun postValidateUser(@Body userNamePasswordModel: UserNamePasswordModel): Boolean {
		try {
			val result = userRepository.validateUser(
				userNamePasswordModel.userName,
				userNamePasswordModel.password
			)
			return result != null
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return false
	}

	@Put("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun put(@Body userUpsertModel: UserUpsertModel): HttpResponse<UserModel?> {
		try {
			val updateModel = userUpsertModel.toRecord(hashIdService)
			userRepository.updateUser(updateModel)
			cacheService.delete("${cachePrefix}_all")
			cacheService.delete("${cachePrefix}_active_all")
			cacheService.delete("${cachePrefix}_${userUpsertModel.id}")
			return HttpResponse.noContent()
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}

	@Delete("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun del(id: String): HttpResponse<String> {
		try {
			userRepository.softDelete(id)
			cacheService.delete("${cachePrefix}_all")
			cacheService.delete("${cachePrefix}_active_all")
			cacheService.delete("${cachePrefix}_${id}")
			return HttpResponse.noContent()
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}

	@Delete("/harddelete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun delHard(id: String): HttpResponse<String> {
		try {
			userRepository.delete(id)
			cacheService.delete("${cachePrefix}_all")
			cacheService.delete("${cachePrefix}_active_all")
			cacheService.delete("${cachePrefix}_${id}")
			return HttpResponse.noContent()
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}
}