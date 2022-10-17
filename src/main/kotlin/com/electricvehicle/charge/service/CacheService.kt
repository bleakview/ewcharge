package com.electricvehicle.charge.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.lettuce.core.KeyScanCursor
import io.lettuce.core.ScanArgs
import io.lettuce.core.api.StatefulRedisConnection
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Singleton
class CacheService(
	var statefulRedisConnection: StatefulRedisConnection<String, String>
) {
	private val log: Logger = LoggerFactory.getLogger(CacheService::class.java)
	private val mapper = jacksonObjectMapper()

	/**
	 * Puts new data to Redis Cache
	 */
	fun putData(key: String, value: Any) {
		try {
			val commands = statefulRedisConnection.sync()
			commands[key] = mapper.writeValueAsString(value)
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
	}

	/**
	 * get data from Redis Cache. Used for Lists
	 */
	fun <T> getData(key: String, classType: Class<T>): T? {
		try {
			val commands = statefulRedisConnection.sync()
			if (commands.exists(key) == 1L) {
				val value = mapper.readValue(commands[key], classType)
				return value
			} else {
				return null
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return null
	}

	/**
	 * get data from Redis Cache used for normal data types
	 */
	fun <T> getData(key: String): T? {
		try {
			val commands = statefulRedisConnection.sync()
			if (commands.exists(key) == 1L) {
				val value = mapper.readValue(commands[key], object : TypeReference<T?>() {})
				return value
			} else {
				return null
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return null
	}

	/**
	 * Deletes data from Redis Cache
	 */
	fun delete(key: String) {
		try {
			val commands = statefulRedisConnection.sync()
			commands.del(key)
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
	}

	/**
	 * Deletes all data from Redis Cache with the given [prefix]
	 */
	fun deleteAll(prefix: String) {
		try {
			val pattern = "${prefix}*"
			var cursor: KeyScanCursor<String> =
				statefulRedisConnection.sync().scan(ScanArgs.Builder.limit(50).match(pattern))
			if (cursor.isFinished) {
				deleteCursor(cursor)
			} else {
				while (!cursor.isFinished) {
					deleteCursor(cursor)
					cursor = statefulRedisConnection.sync().scan(cursor, ScanArgs.Builder.limit(50).match(pattern))
				}
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
	}

	/**
	 * Deletes all given keys with [cursor]
	 */
	private fun deleteCursor(cursor: KeyScanCursor<String>) {
		for (key in cursor.keys) {
			statefulRedisConnection.sync().del(key)
		}
	}

	/**
	 * Get number of keys in database
	 */
	fun count(): Long {
		try {
			val commands = statefulRedisConnection.sync()
			return commands.dbsize()
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return -1
	}
}