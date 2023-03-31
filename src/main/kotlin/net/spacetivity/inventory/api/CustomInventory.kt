package net.spacetivity.inventory.api

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import net.spacetivity.inventory.logic.InventoryControllerImpl
import net.spacetivity.inventory.utils.MathUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

abstract class CustomInventory(val holder: Player, val size: InventorySize, val title: String, val closeable: Boolean) {

    lateinit var rawInventory: Inventory
    private lateinit var controller: InventoryController

    fun construct() {
        this.rawInventory = Bukkit.createInventory(this.holder, this.size.rows * this.size.columns, Component.text(this.title))
        this.controller = InventoryControllerImpl(this)
        this.controller.constructEmptyContent()
        setupInventory(this.holder, this.controller)

        for (content in this.controller.contents) {
            val position: InventoryPosition = content.key
            val interactiveItem: InteractiveItem = content.value ?: continue
            rawInventory.setItem(MathUtils.positionToSlot(position, this.size.columns), interactiveItem.item)
        }
    }

    abstract fun setupInventory(holder: Player, controller: InventoryController)

}