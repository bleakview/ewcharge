package com.electricvehicle.charge.model

import com.electricvehicle.charge.entity.UserEntity
import com.electricvehicle.charge.service.HashIdService
import javax.validation.constraints.NotBlank

data class UserUpsertModel(
	@field:NotBlank var id: String,
	@field:NotBlank val isActive: Boolean,
	@field:NotBlank val name: String,
	@field:NotBlank val password: String,
	@field:NotBlank val role: String

)

/**
 * converts model to entity
 */
fun UserUpsertModel.toRecord(hashIdService: HashIdService): UserEntity = UserEntity(
	id = hashIdService.decodeHash(id),
	isActive = isActive,
	userName = name,
	password = password,
	role = role
)