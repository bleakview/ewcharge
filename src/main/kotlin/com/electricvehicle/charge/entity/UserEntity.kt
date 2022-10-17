package com.electricvehicle.charge.entity

import com.electricvehicle.charge.model.UserModel
import com.electricvehicle.charge.service.HashIdService

data class UserEntity(
	override var id: Long,
	override var isActive: Boolean,
	val userName: String,
	var password: String,
	val role: String
) : IBaseEntity

/**
 * converts Entity to Model
 */
fun UserEntity.toUserModel(hashIdService: HashIdService): UserModel =
	UserModel(
		id = hashIdService.encodeHash(this.id),
		isActive = isActive,
		name = userName,
		password = "*********",
		role = role
	)