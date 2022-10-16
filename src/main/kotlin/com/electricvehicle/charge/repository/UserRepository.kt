package com.electricvehicle.charge.repository


import com.electricvehicle.charge.entity.UserEntity
import com.electricvehicle.charge.service.DatabaseService
import com.electricvehicle.charge.service.HashIdService
import com.electricvehicle.charge.table.UsersTable
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Singleton
class UserRepository(
	private val hashIdService: HashIdService,
	private val databaseService: DatabaseService
) :
	BaseRepository<UsersTable, UserEntity>(hashIdService, databaseService, UsersTable) {
	private val log: Logger = LoggerFactory.getLogger(UserRepository::class.java)
	fun getUserByUserName(username: String): UserEntity? {
		var userEntity: UserEntity? = null
		transaction(db) {
			userEntity = UsersTable
				.select { UsersTable.userName eq username }
				.limit(1)
				.map { it.toRecord() }
				.firstOrNull()
		}
		return userEntity
	}

	fun validateUser(userName: String, password: String): UserEntity? {
		val userEntity = getUserByUserName(userName)
		if ((userEntity != null) &&
			userEntity.isActive &&
			(BCrypt.checkpw(password, userEntity.password))
		) {
			return userEntity
		}
		return null
	}

	fun addUser(userEntity: UserEntity): UserEntity? {
		var recordId: Long = 0
		try {
			if (getUserByUserName(userEntity.userName) != null) {
				return null
			}
			userEntity.password = BCrypt.hashpw(userEntity.password, BCrypt.gensalt())
			transaction(db) {
				recordId = UsersTable.insertAndGetId {
					insertStatement(it, userEntity)
				}.value
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
			return null
		}
		return find(recordId)
	}

	fun updateUser(userEntity: UserEntity): UserEntity? {
		try {
			if (find(userEntity.id) == null) {
				return null
			}
			if (getUserByUserName(userEntity.userName) != null) {
				return null
			}
			userEntity.password = BCrypt.hashpw(userEntity.password, BCrypt.gensalt())
			transaction(db) {
				UsersTable.update({ UsersTable.id eq userEntity.id }) {
					this.updateStatement(it, userEntity)
				}
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
			return null
		}
		return find(userEntity.id)
	}
}