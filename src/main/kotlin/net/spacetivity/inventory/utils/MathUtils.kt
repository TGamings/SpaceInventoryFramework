package net.spacetivity.inventory.utils

import net.spacetivity.inventory.item.InventoryPosition

object MathUtils {

    fun slotToPosition(slot: Int, columns: Int): InventoryPosition {
        val row = slot / columns
        val column = slot % columns
        return InventoryPosition(row, column)
    }

    fun positionToSlot(position: InventoryPosition, columns: Int): Int {
        println(position.row * columns + position.column)
        return position.row * columns + position.column
    }

}