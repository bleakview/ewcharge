package com.electricvehicle.charge.entity

import com.electricvehicle.charge.model.ChargerPortModel
import com.electricvehicle.charge.service.HashIdService

data class ChargerPortEntity(
	override var id: Long,
	override var isActive: Boolean,
	val name: String,
	val latitude: Double,
	val longitude: Double,
	val powerInWatt: Int,
	val chargerPlugTypeName: String
) : IBaseEntity

fun ChargerPortEntity.toChargerPortModel(hashIdService: HashIdService): ChargerPortModel =
	ChargerPortModel(
		id = hashIdService.encodeHash(this.id),
		isActive = isActive,
		name = name,
		latitude = latitude,
		longitude = longitude,
		powerInWatt = powerInWatt,
		chargerPlugTypeName = chargerPlugTypeName
	)

