package com.electricvehicle.charge.repository

import com.electricvehicle.charge.entity.RefreshTokenEntity
import com.electricvehicle.charge.service.CacheService
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@Singleton
class RefreshTokenRepository(private val cacheService: CacheService) {
	private val cachePrefix = "RefreshTokenRepository"
	private val log: Logger = LoggerFactory.getLogger(RefreshTokenRepository::class.java)

	/**
	 * save refresh token to Redis
	 */
	fun save(username: String, refreshToken: String, revoked: Boolean): RefreshTokenEntity {
		val refreshTokenEntity = RefreshTokenEntity(
			id = refreshToken,
			username = username,
			refreshToken = refreshToken,
			revoked = revoked
		)
		cacheService.putData("${cachePrefix}_${refreshToken}", refreshTokenEntity)
		cacheService.putData("${cachePrefix}_${username}", "${cachePrefix}_${refreshToken}")
		return refreshTokenEntity
	}

	/**
	 * Find refresh token
	 */
	fun findByRefreshToken(refreshToken: String): Optional<RefreshTokenEntity> {
		log.error("${cachePrefix}_${refreshToken}")
		val refreshTokenEntity = cacheService.getData("${cachePrefix}_${refreshToken}") as? RefreshTokenEntity
		return Optional.ofNullable(refreshTokenEntity)
	}

	/**
	 * Update Token info by username
	 */
	fun updateByUserName(username: String, revoked: Boolean): String? {
		val refreshTokenKey = cacheService.getData("${cachePrefix}_${username}") as? String
		if (refreshTokenKey != null) {
			val refreshTokenEntity = cacheService.getData(refreshTokenKey) as? RefreshTokenEntity
			if (refreshTokenEntity != null) {
				refreshTokenEntity.revoked = revoked
				cacheService.putData(refreshTokenKey, refreshTokenEntity)
			}
			return refreshTokenKey
		}
		return null
	}

	/**
	 * Deletes all tokens
	 */
	fun deleteAll() {
		cacheService.deleteAll(cachePrefix)
	}

	/**
	 * Get token count
	 */
	fun count(): Long {
		return cacheService.count()
	}
}