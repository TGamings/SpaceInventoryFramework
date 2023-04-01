package net.spacetivity.inventory.api

data class InventorySize(val rows: Int, val columns: Int) {

    fun toBukkitSlot(): Int {
        if (rows <= 0) throw IllegalArgumentException("Minimum row count: 1, Current: $rows")
        if (columns <= 0) throw IllegalArgumentException("Minimum column count: 1, Current: $columns")
        return rows * columns
    }

    companion object {
        fun of(rows: Int, columns: Int) = InventorySize(rows, columns)
    }

}
