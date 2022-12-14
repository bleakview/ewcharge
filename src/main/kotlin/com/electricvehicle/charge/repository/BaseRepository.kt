package com.electricvehicle.charge.repository

import com.electricvehicle.charge.entity.IBaseEntity
import com.electricvehicle.charge.service.DatabaseService
import com.electricvehicle.charge.service.HashIdService
import com.electricvehicle.charge.table.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class BaseRepository<T : BaseTable<U>, U : IBaseEntity>(
	private val hashIdService: HashIdService,
	private val databaseService: DatabaseService,
	private val longIdTable: T
) {
	var db: Database? = databaseService.getDBInstance()
	private val log: Logger = LoggerFactory.getLogger(BaseRepository::class.java)

	/**
	 * Get all items
	 */
	fun getAll(): List<IBaseEntity>? {
		var resultList: List<IBaseEntity>? = null
		try {
			transaction(db) {
				resultList = longIdTable.selectAll().map { t -> t.toRecord() }
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return resultList

	}

	/**
	 * Gets all active items
	 */
	fun getAllActive(): List<IBaseEntity>? {
		var resultList: List<IBaseEntity>? = null
		try {
			transaction(db) {
				resultList = longIdTable.select { longIdTable.isActive eq true }
					.map { t -> t.toRecord() }
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return resultList

	}

	/**
	 * Find item by [id]
	 */
	fun find(id: Long): U? {
		var result: U? = null
		try {
			transaction(db) {
				result = longIdTable
					.select { longIdTable.id eq id and longIdTable.isActive }
					.limit(1)
					.map { it.toRecord() }
					.firstOrNull()
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return result
	}

	/**
	 * find item by [hashId]
	 */
	fun find(hashId: String): U? {
		var result: U? = null
		try {
			val id = hashIdService.decodeHash(hashId)
			result = find(id)
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
		return result
	}

	/**
	 * Save entity to database
	 */
	fun save(baseEntity: U): U? {
		var recordId: Long = 0
		try {
			transaction(db) {
				recordId = longIdTable.insertAndGetId {
					insertStatement(it, baseEntity)
				}.value
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
			return null
		}
		return find(recordId)
	}

	/**
	 * Updates database
	 */
	fun update(baseEntity: U): U? {
		try {
			transaction(db) {
				longIdTable.update({ longIdTable.id eq baseEntity.id }) {
					this.updateStatement(it, baseEntity)
				}
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
			return null
		}
		return find(baseEntity.id)
	}

	/**
	 * Disactivates item by [id]
	 */
	fun softDelete(id: Long) {
		try {
			transaction(db) {
				longIdTable.update({ longIdTable.id eq id }) {
					it[isActive] = false
				}
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
	}

	/**
	 * Disactivates element by [hashId]
	 */
	fun softDelete(hashId: String) {
		try {
			val id = hashIdService.decodeHash(hashId)
			softDelete(id)
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
	}

	/**
	 * Deletes element from database
	 */
	fun delete(id: Long) {
		try {
			transaction(db) {
				longIdTable.deleteWhere { longIdTable.id eq id }
			}
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
	}

	/**
	 * Deletes element with given [hashId]
	 */
	fun delete(hashId: String) {
		try {
			val id = hashIdService.decodeHash(hashId)
			delete(id)
		} catch (ex: Exception) {
			log.error(ex.stackTraceToString())
		}
	}

	/**
	 * Add toRecord method to ResultRow
	 */
	protected fun ResultRow.toRecord() = longIdTable.toEntity(this)
}