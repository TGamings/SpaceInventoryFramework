package net.spacetivity.inventory

import net.spacetivity.inventory.logic.InventoryPlayerListener
import net.spacetivity.inventory.logic.InventoryRepository
import org.bukkit.plugin.java.JavaPlugin

class SpaceInventoryBootstrap : JavaPlugin() {

    lateinit var inventoryRepository: InventoryRepository

    init {
        instance = this
    }

    override fun onEnable() {
        this.inventoryRepository = InventoryRepository()
        server.pluginManager.registerEvents(InventoryPlayerListener(), this)
    }

    override fun onDisable() {

    }

    companion object {
        @JvmStatic
        lateinit var instance: SpaceInventoryBootstrap
            private set
    }

}