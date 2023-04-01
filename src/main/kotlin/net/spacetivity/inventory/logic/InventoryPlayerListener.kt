package net.spacetivity.inventory.logic

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.spacetivity.inventory.SpaceInventoryBootstrap
import net.spacetivity.inventory.api.CustomInventory
import net.spacetivity.inventory.utils.MathUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent

class InventoryPlayerListener(private val bootstrap: SpaceInventoryBootstrap) : Listener {

    private val inventoryRepository: InventoryRepository = this.bootstrap.inventoryRepository

    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        if (!this.inventoryRepository.playerInventories.contains(player.uniqueId)) return
        if (event.clickedInventory != player.openInventory.topInventory) return
        if (!isInventoryValid(player, event.view.title())) return

        val customInventory: CustomInventory = this.inventoryRepository.playerInventories[player.uniqueId]!!
        val position = MathUtils.slotToPosition(event.slot, customInventory.size.columns)
        val isClickAllowed = customInventory.usableSlots.contains(position)

        event.isCancelled = !isClickAllowed

        val currentItem = customInventory.controller.getItem(position)
        if (currentItem?.action != null) currentItem.runAction(position, event)
    }

    @EventHandler
    fun onPlayerInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder !is Player) return

        val player = event.player as Player

        if (!this.inventoryRepository.playerInventories.contains(player.uniqueId)) return

        val customInventory: CustomInventory = this.inventoryRepository.playerInventories[player.uniqueId]!!

        if (!isInventoryValid(player, event.view.title())) return

        if (!customInventory.closeable) {
            Bukkit.getScheduler().runTask(bootstrap.plugin, Runnable { customInventory.open() })
            return
        }

        this.inventoryRepository.unregister(player.uniqueId)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (this.inventoryRepository.playerInventories.contains(player.uniqueId))
            this.inventoryRepository.unregister(player.uniqueId)
    }

    private fun isInventoryValid(player: Player, title: Component): Boolean {
        val openInventoryName: String = PlainTextComponentSerializer.plainText().serialize(title)
        val customInventory: CustomInventory = this.inventoryRepository.playerInventories[player.uniqueId]!!
        val playerInventoryName: String = customInventory.title
        return openInventoryName == playerInventoryName
    }

}