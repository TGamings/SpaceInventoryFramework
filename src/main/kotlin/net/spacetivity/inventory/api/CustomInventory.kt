package net.spacetivity.inventory.api

import org.bukkit.entity.Player

abstract class CustomInventory(
    val size: InventorySize = InventorySize(1, 9),
    val title: String = "inventory.tag.notSet",
    val closeable: Boolean = true
) {

    abstract fun setupInventory(holder: Player, controller: InventoryController)

}