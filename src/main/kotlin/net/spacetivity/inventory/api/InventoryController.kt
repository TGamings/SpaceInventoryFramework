package net.spacetivity.inventory.api

import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import org.bukkit.Material
import org.bukkit.inventory.Inventory

interface InventoryController {

    val inventory: CustomInventory
    val inventorySlotCount: Int
    val contents: MutableMap<InventoryPosition, InteractiveItem?>

    fun constructEmptyContent()
    // item functions

    fun placeholder(pos: InventoryPosition, material: Material)
    fun placeholder(row: Int, column: Int, material: Material)
    fun setItem(row: Int, column: Int, item: InteractiveItem)
    fun setItem(pos: InventoryPosition, item: InteractiveItem)
    fun addItem(item: InteractiveItem)
    fun addItemToRandomPosition(item: InteractiveItem)
    fun removeItem(displayName: String)
    fun removeItem(material: Material)

    fun fill(direction: FillDirection, item: InteractiveItem, vararg positions: InventoryPosition)

    // slot functions

    fun clearSlot(pos: InventoryPosition)

    fun isPositionTaken(pos: InventoryPosition): Boolean
    /**
     * Return value is null if there is no empty slot
     */
    fun getFirstEmptySlot(): InventoryPosition?
    fun getItem(position: InventoryPosition): InteractiveItem?
    fun getItem(row: Int, column: Int): InteractiveItem?
    fun findFirstItemWithType(material: Material): Pair<InventoryPosition, InteractiveItem>?

    // utility functions

    fun getRawInventory(): Inventory

    enum class FillDirection {
        ROW,
        RECTANGLE,
        LEFT_BORDER,
        RIGHT_BORDER,
        TOP_BORDER,
        BOTTOM_BORDER,
        ALL_BORDERS
    }
}