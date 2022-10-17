package com.electricvehicle.charge.entity

import com.electricvehicle.charge.service.HashIdService

interface IBaseEntity {
	var id: Long
	var isActive: Boolean
}
