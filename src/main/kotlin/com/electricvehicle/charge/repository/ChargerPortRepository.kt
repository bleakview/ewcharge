package com.electricvehicle.charge.repository


import com.electricvehicle.charge.entity.ChargerPortEntity
import com.electricvehicle.charge.service.DatabaseService
import com.electricvehicle.charge.service.HashIdService
import com.electricvehicle.charge.table.ChargerPortsTable

import jakarta.inject.Singleton


@Singleton
class ChargerPortRepository(
	private val hashIdService: HashIdService,
	private val databaseService: DatabaseService
) :
	BaseRepository<ChargerPortsTable, ChargerPortEntity>(hashIdService, databaseService, ChargerPortsTable)