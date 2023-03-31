package net.spacetivity.inventory.utils

import net.spacetivity.inventory.item.InventoryPosition

object MathUtils {

    /**
     * Refactors a bukkit slot (row * column) to a {@see InventoryPosition}
     */
    fun slotToPosition(slot: Int): InventoryPosition {
        val row = slot / 9
        val column = slot % 9
        return InventoryPosition(row, column)
    }

    /**
     * Refactors a {@see InventoryPosition} to a slot (row * column)
     */
    fun positionToSlot(position: InventoryPosition): Int {
        return position.row * position.column
    }

}