package com.electricvehicle.charge.table

import com.electricvehicle.charge.entity.IBaseEntity
import com.electricvehicle.charge.service.HashIdService
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement

abstract class BaseTable<T : IBaseEntity>(tableName: String = "") : LongIdTable(name = tableName) {
	val isActive: Column<Boolean> = bool("is_active")

	/**
	 * Abstract method converting [rows] to entites [T]
	 */
	abstract fun toEntity(row: ResultRow): T

	/**
	 * Abstract method for inserting
	 */
	abstract fun insertStatement(insertStatement: InsertStatement<EntityID<Long>>, baseEntity: T):
			InsertStatement<EntityID<Long>>

	/**
	 * Abstract method for updating
	 */
	abstract fun updateStatement(updateStatement: UpdateStatement, baseEntity: T): UpdateStatement

}