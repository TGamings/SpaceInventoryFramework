package net.spacetivity.inventory

import net.spacetivity.inventory.logic.InventoryPlayerListener
import net.spacetivity.inventory.logic.InventoryRepository
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SpaceInventoryBootstrap {

    lateinit var plugin: JavaPlugin
    lateinit var inventoryRepository: InventoryRepository

    fun init(plugin: JavaPlugin) {
        instance = this
        this.plugin = plugin
        this.inventoryRepository = InventoryRepository()
        Bukkit.getServer().pluginManager.registerEvents(InventoryPlayerListener(this), plugin)
    }

    companion object {
        @JvmStatic
        lateinit var instance: SpaceInventoryBootstrap
            private set
    }

}