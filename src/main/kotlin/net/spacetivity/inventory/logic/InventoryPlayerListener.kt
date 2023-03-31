package net.spacetivity.inventory.logic

import net.spacetivity.inventory.SpaceInventoryBootstrap
import net.spacetivity.inventory.TestInventory
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class InventoryPlayerListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val customInventory = TestInventory(player)

        SpaceInventoryBootstrap.instance.inventoryRepository.register(customInventory)

        Bukkit.getScheduler().runTaskLater(SpaceInventoryBootstrap.instance, Runnable {
            SpaceInventoryBootstrap.instance.inventoryRepository.open(player)
        }, 20 * 3)

    }

}