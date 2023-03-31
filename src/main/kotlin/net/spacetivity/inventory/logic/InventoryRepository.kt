package net.spacetivity.inventory.logic

import net.spacetivity.inventory.api.CustomInventory
import org.bukkit.entity.Player
import java.util.*

class InventoryRepository {

    private val playerInventories: MutableMap<UUID, CustomInventory> = mutableMapOf()

    fun open(player: Player) {
        val customInventory: CustomInventory = playerInventories[player.uniqueId]!!
        player.openInventory(customInventory.rawInventory)
    }

    fun register(inventory: CustomInventory) {
        inventory.construct()
        this.playerInventories[inventory.holder.uniqueId] = inventory
    }

    fun unregister(uniqueId: UUID) {
        this.playerInventories.remove(uniqueId)
    }

}