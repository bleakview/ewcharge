package com.electricvehicle.charge.model

import com.electricvehicle.charge.entity.ChargerPortEntity
import com.electricvehicle.charge.service.HashIdService
import javax.validation.constraints.NotBlank

data class ChargerPortUpsertModel(
	@field:NotBlank var id: String,
	@field:NotBlank val isActive: Boolean,
	@field:NotBlank val name: String,
	@field:NotBlank val latitude: Double,
	@field:NotBlank val longitude: Double,
	@field:NotBlank val powerInWatt: Int,
	@field:NotBlank val chargerPlugTypeName: String,
)

/**
 * converts model to entity
 */
fun ChargerPortUpsertModel.toRecord(hashIdService: HashIdService): ChargerPortEntity = ChargerPortEntity(
	id = hashIdService.decodeHash(id),
	isActive = isActive,
	name = name,
	latitude = latitude,
	longitude = longitude,
	powerInWatt = powerInWatt,
	chargerPlugTypeName = chargerPlugTypeName,
)