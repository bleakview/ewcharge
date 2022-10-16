package com.electricvehicle.charge.service

import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.hashids.Hashids

/**
 * Service used to change default long ids to string. This is not done for security.
 * Security through obfuscation is dumb this is done to hide information of how many records
 * database has.
 */
@Singleton
class HashIdService {
	@Value("\${hashid.salt}")
	private lateinit var salt: String

	@Value("\${hashid.length}")
	private var length: Int = 0

	fun encodeHash(id: Long): String {
		val hashids = Hashids(salt, length)
		return hashids.encode(id)
	}

	fun decodeHash(hashid: String): Long {
		val hashids = Hashids(salt, length)
		if (hashids.decode(hashid).isNotEmpty()) {
			return hashids.decode(hashid)[0]
		}
		return 0
	}
}