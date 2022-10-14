package com.electricvehicle.charge.table

import com.electricvehicle.charge.entity.ChargerPortEntity
import com.electricvehicle.charge.service.HashIdService
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement


object ChargerPortsTable : BaseTable<ChargerPortEntity>(tableName = "charger_plug_types") {
	val name: Column<String> = varchar("name", 50)
	val latitude: Column<Double> = double("latitude")
	val longitude: Column<Double> = double("longitude")
	val powerInWatt: Column<Int> = integer("power_in_watts")
	val chargerPlugTypeName = varchar("charger_plug_type_name", 50)
	override fun toRecord(hashIdService: HashIdService, row: ResultRow): ChargerPortEntity =
		ChargerPortEntity(
			id = row[id].value,
			isActive = row[isActive],
			name = row[name],
			latitude = row[latitude],
			longitude = row[longitude],
			powerInWatt = row[powerInWatt],
			chargerPlugTypeName = row[chargerPlugTypeName]
		)

	override fun insertStatement(
		insertStatement: InsertStatement<EntityID<Long>>,
		chargerPortEntity: ChargerPortEntity
	): InsertStatement<EntityID<Long>> {
		insertStatement[name] = chargerPortEntity.name
		insertStatement[isActive] = chargerPortEntity.isActive
		insertStatement[latitude] = chargerPortEntity.latitude
		insertStatement[longitude] = chargerPortEntity.longitude
		insertStatement[powerInWatt] = chargerPortEntity.powerInWatt
		insertStatement[chargerPlugTypeName] = chargerPortEntity.chargerPlugTypeName
		return insertStatement
	}

	override fun updateStatement(
		updateStatement: UpdateStatement,
		chargerPortEntity: ChargerPortEntity
	): UpdateStatement {
		updateStatement[name] = chargerPortEntity.name
		updateStatement[isActive] = chargerPortEntity.isActive
		updateStatement[latitude] = chargerPortEntity.latitude
		updateStatement[longitude] = chargerPortEntity.longitude
		updateStatement[powerInWatt] = chargerPortEntity.powerInWatt
		updateStatement[chargerPlugTypeName] = chargerPortEntity.chargerPlugTypeName
		return updateStatement
	}
}