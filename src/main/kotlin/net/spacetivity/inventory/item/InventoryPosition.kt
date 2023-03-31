package net.spacetivity.inventory.item

import net.spacetivity.inventory.utils.MathUtils

data class InventoryPosition(val row: Int, val column: Int) {

    companion object {

        fun of(row: Int, column: Int): InventoryPosition {
            return InventoryPosition(row, column)
        }

        fun of (slot: Int): InventoryPosition {
            return MathUtils.slotToPosition(slot)
        }

    }

}