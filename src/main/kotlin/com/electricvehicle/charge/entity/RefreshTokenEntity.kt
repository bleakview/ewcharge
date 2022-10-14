package com.electricvehicle.charge.entity

import com.electricvehicle.charge.helper.CommonRoles
import java.time.Instant

data class RefreshTokenEntity(
	var id: String? = null,
	var username: String,
	var refreshToken: String,
	var revoked: Boolean,
	var role: String = CommonRoles.View,
	var dateCreated: Instant? = null
)
