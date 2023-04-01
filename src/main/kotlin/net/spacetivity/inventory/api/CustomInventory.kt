package net.spacetivity.inventory.api

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.SpaceInventoryBootstrap
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import net.spacetivity.inventory.logic.InventoryControllerImpl
import net.spacetivity.inventory.utils.MathUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

abstract class CustomInventory(private val holder: Player, val size: InventorySize, val title: String, val closeable: Boolean) {

    val inventorySlotCount: Int = this.size.rows * this.size.columns
    lateinit var rawInventory: Inventory
    lateinit var controller: InventoryController
    lateinit var usableSlots: MutableList<InventoryPosition>

    init {
        safeInventory()
    }

    private fun safeInventory() {
        construct()
        SpaceInventoryBootstrap.instance.inventoryRepository.playerInventories[this.holder.uniqueId] = this
    }

    private fun construct() {
        this.rawInventory = Bukkit.createInventory(this.holder, this.size.rows * this.size.columns, Component.text(this.title))
        this.controller = InventoryControllerImpl(this)
        this.controller.constructEmptyContent()
        setupInventory(this.holder, this.controller)

        this.usableSlots = mutableListOf()
        setupUsableSlots(this.usableSlots)

        updateRawInventory()
    }

    fun updateRawInventory() {
        for (content in this.controller.contents) {
            val position: InventoryPosition = content.key
            val interactiveItem: InteractiveItem = content.value ?: continue
            rawInventory.setItem(MathUtils.positionToSlot(position, this.size.columns), interactiveItem.item)
        }
    }

    fun open() {
        val inventory = SpaceInventoryBootstrap.instance.inventoryRepository.playerInventories[this.holder.uniqueId]!!
        this.holder.openInventory(inventory.rawInventory)
    }

    abstract fun setupInventory(holder: Player, controller: InventoryController)
    abstract fun setupUsableSlots(usableSlots: MutableList<InventoryPosition>)

}