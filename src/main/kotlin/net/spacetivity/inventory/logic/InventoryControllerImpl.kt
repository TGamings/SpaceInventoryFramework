package net.spacetivity.inventory.logic

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.spacetivity.inventory.api.CustomInventory
import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.api.InventoryController.FillDirection.*
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import net.spacetivity.inventory.utils.MathUtils
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import java.util.concurrent.ThreadLocalRandom

class InventoryControllerImpl(override val inventory: CustomInventory) : InventoryController {

    override val inventorySlotCount: Int = inventory.size.rows * inventory.size.columns
    override val contents: MutableMap<InventoryPosition, InteractiveItem?> = mutableMapOf()

    // constructs the default content map
    override fun constructEmptyContent() {
        for (i in 0..this.inventorySlotCount) this.contents[MathUtils.slotToPosition(i)] = null
    }

    override fun placeholder(pos: InventoryPosition, material: Material) {
        setItem(pos, InteractiveItem.placeholder(material))
    }

    override fun placeholder(row: Int, column: Int, material: Material) {
        setItem(row, column, InteractiveItem.placeholder(material))
    }

    override fun setItem(row: Int, column: Int, item: InteractiveItem) {
        contents[InventoryPosition.of(row, column)] = item
    }

    override fun setItem(pos: InventoryPosition, item: InteractiveItem) {
        contents[pos] = item
    }

    override fun addItem(item: InteractiveItem) {
        val firstEmptySlot = getFirstEmptySlot() ?: return
        setItem(firstEmptySlot, item)
    }

    override fun addItemToRandomPosition(item: InteractiveItem) {
        val randomSlotCount = ThreadLocalRandom.current().nextInt(this.inventorySlotCount)
        val randomPosition = MathUtils.slotToPosition(randomSlotCount)
        if (isPositionTaken(randomPosition)) return
        setItem(randomPosition, item)
    }

    override fun removeItem(displayName: String) {
        val tempEntries: MutableMap<InventoryPosition, InteractiveItem?> = this.contents
        for (entry in tempEntries) {
            val position = entry.key
            val interactiveItem = entry.value ?: continue
            val serializer = PlainTextComponentSerializer.plainText()
            if (serializer.serialize(interactiveItem.item.itemMeta.displayName()!!).equals(displayName, true)) {
                this.contents[position] = null
                getRawInventory().remove(interactiveItem.item)
            }
        }
    }

    override fun removeItem(material: Material) {
        val tempEntries: MutableMap<InventoryPosition, InteractiveItem?> = this.contents
        for (entry in tempEntries) {
            val position = entry.key
            val interactiveItem = entry.value ?: continue
            if (interactiveItem.item.type == material) {
                this.contents[position] = null
                getRawInventory().remove(interactiveItem.item)
            }
        }
    }

    override fun fill(direction: InventoryController.FillDirection, item: InteractiveItem, vararg positions: InventoryPosition) {
        when (direction) {
            ROW -> {
                if (positions.size > 1)
                    throw IllegalArgumentException("To fill a row only 1 position is allowed. Used positions: ${positions.size}")

                val startSlot = MathUtils.positionToSlot(positions[0])

                for (currentSlot in startSlot..(startSlot + this.inventory.size.columns)) {
                    val currentPosition = MathUtils.slotToPosition(currentSlot)
                    setItem(currentPosition, item)
                }
            }

            RECTANGLE -> {
                if (positions.size != 2)
                    throw IllegalArgumentException("Only to positions are allowed to create an rectangle!")

                val fromPos: InventoryPosition = positions[0]
                val toPos: InventoryPosition = positions[1]

                val fromRow = fromPos.row
                val fromColumn = fromPos.column

                val toRow = toPos.row
                val toColumn = toPos.column

                for (row in fromRow..toRow) {
                    for (column in fromColumn..toColumn) {
                        if (row != fromRow && row != toRow && column != fromColumn && column != toColumn) continue
                        setItem(row, column, item)
                    }
                }
            }

            LEFT_BORDER -> {
                for (slot in 0 until this.inventorySlotCount step this.inventory.size.rows) {
                    val currentPosition = InventoryPosition.of(slot)
                    setItem(currentPosition, item)
                }
            }

            RIGHT_BORDER -> {
                for (slot in this.inventory.size.rows - 1 until this.inventorySlotCount step this.inventory.size.rows) {
                    val currentPosition = InventoryPosition.of(slot)
                    setItem(currentPosition, item)
                }
            }

            TOP_BORDER -> {
                for (slot in 0 until this.inventory.size.columns) {
                    val currentPosition = InventoryPosition.of(slot)
                    setItem(currentPosition, item)
                }
            }

            BOTTOM_BORDER -> {
                val size = this.inventorySlotCount
                val firstColumnInLastRow = size - this.inventory.size.rows

                for (slot in firstColumnInLastRow until size) {
                    val currentPosition = InventoryPosition.of(slot)
                    setItem(currentPosition, item)
                }
            }

            ALL_BORDERS -> {
                fill(TOP_BORDER, item)
                fill(RIGHT_BORDER, item)
                fill(BOTTOM_BORDER, item)
                fill(LEFT_BORDER, item)
            }
        }
    }

    override fun clearSlot(pos: InventoryPosition) {
        this.contents[pos] = null
        getRawInventory().clear(MathUtils.positionToSlot(pos))
    }

    override fun isPositionTaken(pos: InventoryPosition): Boolean {
        return this.contents[pos] == null
    }

    override fun getFirstEmptySlot(): InventoryPosition? {
        var emptyPosition: InventoryPosition? = null
        for (position in this.contents.keys) if (this.contents[position] == null) emptyPosition = position
        return emptyPosition
    }

    override fun getItem(position: InventoryPosition): InteractiveItem? {
        TODO("Not yet implemented")
    }

    override fun getItem(row: Int, column: Int): InteractiveItem? {
        TODO("Not yet implemented")
    }

    override fun findFirstItemWithType(material: Material): Pair<InventoryPosition, InteractiveItem>? {
        TODO("Not yet implemented")
    }

    override fun findFirstItemWithName(displayName: String): Pair<InventoryPosition, InteractiveItem>? {
        TODO("Not yet implemented")
    }

    override fun getRawInventory(): Inventory {
        TODO("Not yet implemented")
    }
}