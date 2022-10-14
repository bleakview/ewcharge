package com.electricvehicle.charge.model

import javax.validation.constraints.NotBlank

open class ChargerPortModel(
	@field:NotBlank var id: String,
	@field:NotBlank val isActive: Boolean,
	@field:NotBlank val name: String,
	@field:NotBlank val latitude: Double,
	@field:NotBlank val longitude: Double,
	@field:NotBlank val powerInWatt: Int,
	@field:NotBlank val chargerPlugTypeName: String,
)

