package com.electricvehicle.charge.helper

data class ReturnValue<T>(
	val errorMessage: String? = null,
	val errorCode: Long? = null,
	val value: T? = null
)