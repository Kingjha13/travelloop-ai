package com.example.database

import com.example.models.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(config: ApplicationConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.property("database.url").getString()
            driverClassName = config.property("database.driver").getString()
            username = config.property("database.user").getString()
            password = config.property("database.password").getString()
            maximumPoolSize = config.property("database.maxPoolSize").getString().toInt()
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                TripsTable,
                StopsTable,
                ActivitiesTable,
                PackingItemsTable,
                TripNotesTable,
                BudgetItemsTable
            )
        }
    }
}