package com.electricvehicle.charge.table

import com.electricvehicle.charge.entity.UserEntity
import com.electricvehicle.charge.service.HashIdService
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement

object UsersTable : BaseTable<UserEntity>(tableName = "users") {
	val userName: Column<String> = varchar("username", 100)
	val password: Column<String> = varchar("password", 100)
	val role: Column<String> = varchar("role", 75)
	override fun toRecord(hashIdService: HashIdService, row: ResultRow): UserEntity =
		UserEntity(
			id = row[id].value,
			isActive = row[isActive],
			userName = row[userName],
			password = row[password],
			role = row[role],
		)

	override fun insertStatement(
		insertStatement: InsertStatement<EntityID<Long>>,
		userEntity: UserEntity
	): InsertStatement<EntityID<Long>> {
		insertStatement[userName] = userEntity.userName
		insertStatement[password] = userEntity.password
		insertStatement[role] = userEntity.role
		insertStatement[isActive] = userEntity.isActive
		return insertStatement
	}

	override fun updateStatement(updateStatement: UpdateStatement, userEntity: UserEntity): UpdateStatement {
		updateStatement[userName] = userEntity.userName
		updateStatement[password] = userEntity.password
		updateStatement[role] = userEntity.role
		updateStatement[isActive] = userEntity.isActive
		return updateStatement
	}
}