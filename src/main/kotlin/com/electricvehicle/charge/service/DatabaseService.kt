package com.electricvehicle.charge.service

import com.electricvehicle.charge.helper.ReturnValue
import com.electricvehicle.charge.table.ChargerPortsTable
import com.electricvehicle.charge.table.UsersTable
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class DatabaseService {
	@Value("\${datasources.default.url}")
	private lateinit var url: String

	@Value("\${datasources.default.driverClassName}")
	private lateinit var driver: String

	@Value("\${datasources.default.username}")
	private lateinit var user: String

	@Value("\${datasources.default.password}")
	private lateinit var password: String

	@Value("\${datasources.default.schema}")
	private lateinit var schema: String
	private val log: Logger = LoggerFactory.getLogger(DatabaseService::class.java)
	fun getDBInstance(): Database? {
		try {
			return Database.connect("${url}${schema}", driver = driver, user = user, password = password)
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return null
	}

	fun getDBInstanceWithoutSchema(): Database? {
		try {
			return Database.connect(url, driver = driver, user = user, password = password)
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return null
	}

	fun createSchema(): ReturnValue<Any> {
		try {
			transaction(getDBInstanceWithoutSchema()) {
				val schema = Schema(schema)
				SchemaUtils.createSchema(schema)
			}
			transaction(getDBInstance()) {
				SchemaUtils.create(ChargerPortsTable)
				SchemaUtils.create(UsersTable)
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
			return ReturnValue(errorCode = 500, errorMessage = ex.message)
		}
		return ReturnValue()
	}

	fun dropSchema(): ReturnValue<Any> {
		try {
			transaction(getDBInstance()) {
				val schema = Schema(schema)
				SchemaUtils.dropSchema(schema)
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
			return ReturnValue(errorCode = 500, errorMessage = ex.message)
		}
		return ReturnValue()
	}
}