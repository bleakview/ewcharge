package com.electricvehicle.charge.model

import javax.validation.constraints.NotBlank

open class UserModel(
	@field:NotBlank var id: String,
	@field:NotBlank val isActive: Boolean,
	@field:NotBlank val name: String,
	@field:NotBlank val password: String,
	@field:NotBlank val role: String
)