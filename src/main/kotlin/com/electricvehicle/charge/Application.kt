package com.electricvehicle.charge

import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme

@OpenAPIDefinition(
	info = Info(
		title = "ewcharge",
		version = "0.0"
	)
)

@SecurityScheme(
	name = "Authorization",
	type = SecuritySchemeType.HTTP,
	paramName = "Authorization",
	`in` = SecuritySchemeIn.HEADER,
	scheme = "bearer",
	bearerFormat = "jwt",
)
object Api

@SecurityRequirement(name = "Authorization")
fun main(args: Array<String>) {
	run(*args)
}

