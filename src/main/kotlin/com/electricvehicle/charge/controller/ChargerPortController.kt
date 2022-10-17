package com.electricvehicle.charge.controller

import com.electricvehicle.charge.entity.ChargerPortEntity
import com.electricvehicle.charge.entity.toChargerPortModel
import com.electricvehicle.charge.helper.CommonRoles
import com.electricvehicle.charge.model.ChargerPortModel
import com.electricvehicle.charge.model.ChargerPortUpsertModel
import com.electricvehicle.charge.model.toRecord
import com.electricvehicle.charge.repository.ChargerPortRepository
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
@Controller("/chargerport")
@Secured(SecurityRule.IS_AUTHENTICATED)
open class ChargerPortController(
	private val chargerPlugTypeRepository: ChargerPortRepository,
	private val cacheService: CacheService,
	private val hashIdService: HashIdService,
) {
	private val cachePrefix = "ChargerPlugTypeModel"
	private val log: Logger = LoggerFactory.getLogger(CacheService::class.java)

	/**
	 * Used for a getting charge port
	 */
	@Get("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.View, CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun get(id: String): ChargerPortModel? {
		var chargerPlugTypeModel = cacheService.getData("${cachePrefix}_$id", ChargerPortModel::class.java)
		try {
			if (chargerPlugTypeModel == null) {
				chargerPlugTypeModel = chargerPlugTypeRepository.find(id)
					?.toChargerPortModel(hashIdService)
				if (chargerPlugTypeModel != null) {
					cacheService.putData("${cachePrefix}_$id", chargerPlugTypeModel)
				}
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return chargerPlugTypeModel
	}

	/**
	 * Used for getting all chage ports
	 */
	@Get("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.View, CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun getall(): List<ChargerPortModel>? {
		cacheService.deleteAll("Refresh")
		var chargerPlugTypeModel = cacheService.getData("${cachePrefix}_active_all")
				as? List<ChargerPortModel>
		try {
			if (chargerPlugTypeModel == null) {
				val chargerPortEntity = chargerPlugTypeRepository.getAllActive()?.filterIsInstance<ChargerPortEntity>()
				chargerPlugTypeModel = chargerPortEntity?.map { t -> t.toChargerPortModel(hashIdService) }
				if (chargerPlugTypeModel != null) {
					cacheService.putData("${cachePrefix}_active_all", chargerPlugTypeModel)
				}
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return chargerPlugTypeModel
	}

	/**
	 * Used for creating charge port
	 */
	@Post("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.View, CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun post(@Body chargerPortUpsertModel: ChargerPortUpsertModel): HttpResponse<ChargerPortModel?> {
		try {
			val insertModel = chargerPortUpsertModel.toRecord(hashIdService)
			val chargerPlugType = chargerPlugTypeRepository.save(insertModel)
			cacheService.delete("${cachePrefix}_active_all")
			val result = chargerPlugType?.toChargerPortModel(hashIdService)
			return HttpResponse.created(result).header(HttpHeaders.LOCATION, "/chargerport/${result?.id}")
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}

	/**
	 * Used for updating charge port
	 */
	@Put("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.View, CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun put(@Body chargerPortUpsertModel: ChargerPortUpsertModel): HttpResponse<ChargerPortModel?> {
		try {
			val updateModel = chargerPortUpsertModel.toRecord(hashIdService)
			val chargerPlugType = chargerPlugTypeRepository.update(updateModel)
			cacheService.delete("${cachePrefix}_active_all")
			cacheService.delete("${cachePrefix}_${chargerPortUpsertModel.id}")
			return HttpResponse.noContent()
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}

	/**
	 * Used for deleting charge port (It do not deletes charge port it only disactivates it)
	 */
	@Delete("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(SecurityRule.IS_AUTHENTICATED)
	@RolesAllowed(*[CommonRoles.View, CommonRoles.Admin])
	@SecurityRequirement(name = "Authorization")
	fun del(id: String): HttpResponse<String> {
		try {
			chargerPlugTypeRepository.softDelete(id)
			cacheService.delete("${cachePrefix}_active_all")
			cacheService.delete("${cachePrefix}_${id}")
			return HttpResponse.noContent()
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return HttpResponse.serverError()
	}
}