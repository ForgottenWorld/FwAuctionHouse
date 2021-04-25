package me.kaotich00.fwauctionhouse

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import me.kaotich00.fwauctionhouse.services.*
import me.kaotich00.fwauctionhouse.storage.ListingsDao
import me.kaotich00.fwauctionhouse.storage.ListingsDaoImpl
import me.kaotich00.fwauctionhouse.storage.DatabaseConnectionManager
import me.kaotich00.fwauctionhouse.storage.hikari.MySQLDatabaseConnectionManager

class DependenciesModule(private val plugin: FwAuctionHouse) : AbstractModule() {

    fun createInjector(): Injector = Guice.createInjector(this)

    override fun configure() {
        bind(FwAuctionHouse::class.java).toInstance(plugin)

        bind(DatabaseConnectionManager::class.java).toInstance(MySQLDatabaseConnectionManager())

        bind(ListingsDao::class.java).to(ListingsDaoImpl::class.java)

        bind(ListingsService::class.java).to(ListingsServiceImpl::class.java)

        bind(MarketAreaService::class.java).to(MarketAreaServiceImpl::class.java)

        bind(WebApiService::class.java).to(WebApiServiceImpl::class.java)
    }
}