package net.spacetivity.inventory.logic

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.spacetivity.inventory.SpaceInventoryBootstrap
import net.spacetivity.inventory.api.CustomInventory
import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.api.InventoryController.FillType.*
import net.spacetivity.inventory.api.InventoryPagination
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import net.spacetivity.inventory.utils.MathUtils
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import java.util.concurrent.ThreadLocalRandom

class InventoryControllerImpl(override val inventory: CustomInventory) : InventoryController {

    override val inventorySlotCount: Int = this.inventory.inventorySlotCount
    override val contents: MutableMap<InventoryPosition, InteractiveItem?> = mutableMapOf()

    private var pagination: InventoryPagination? = null

    override fun constructEmptyContent() {
        for (i in 0 until this.inventorySlotCount)
            this.contents[MathUtils.slotToPosition(i, this.inventory.size.columns)] = null
    }

    override fun placeholder(pos: InventoryPosition, vararg material: Material) {
        if (material.isEmpty()) {
            val exception = NullPointerException("Default placeholder material is null!")
            val placeholderMaterial = SpaceInventoryBootstrap.instance.defaultPlaceholderMaterial ?: throw exception
            placeholder(pos, placeholderMaterial)
        } else {
            placeholder(pos, material[0])
        }
    }

    override fun placeholder(pos: InventoryPosition, material: Material) {
        setItem(pos, InteractiveItem.placeholder(material))
    }

    override fun placeholder(row: Int, column: Int, material: Material) {
        setItem(row, column, InteractiveItem.placeholder(material))
    }

    override fun setItem(row: Int, column: Int, item: InteractiveItem) {
        this.contents[InventoryPosition.of(row, column)] = item
    }

    override fun setItem(pos: InventoryPosition, item: InteractiveItem) {
        this.contents[pos] = item
    }

    override fun addItem(item: InteractiveItem) {
        val firstEmptySlot = getFirstEmptySlot() ?: return
        setItem(firstEmptySlot, item)
    }

    override fun addItemToRandomPosition(item: InteractiveItem) {
        val randomSlotCount = ThreadLocalRandom.current().nextInt(this.inventorySlotCount)
        val randomPosition = MathUtils.slotToPosition(randomSlotCount, this.inventory.size.columns)
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

    override fun fill(type: InventoryController.FillType, item: InteractiveItem, vararg positions: InventoryPosition) {
        when (type) {
            ROW -> {
                if (positions.size > 1)
                    throw IllegalArgumentException("To fill a row only 1 position is allowed. Used positions: ${positions.size}")

                val startSlot = MathUtils.positionToSlot(positions[0], this.inventory.size.columns)

                for (currentSlot in startSlot..(startSlot + this.inventory.size.columns)) {
                    val currentPosition = MathUtils.slotToPosition(currentSlot, this.inventory.size.columns)
                    setItem(currentPosition, item)
                }
            }

            RECTANGLE -> {
                if (positions.size != 2)
                    throw IllegalArgumentException("Only two positions are allowed to create an rectangle!")

                val fromPos: InventoryPosition = positions[0]
                val toPos: InventoryPosition = positions[1]

                val fromRow = fromPos.row
                val fromColumn = fromPos.column

                val toRow = toPos.row
                val toColumn = toPos.column

                for (row in fromRow..toRow) {
                    for (col in fromColumn..toColumn) {
                        row * this.inventory.size.rows + col
                        setItem(row, col, item)
                    }
                }
            }

            LEFT_BORDER -> {
                for (currentSlot in 0 until this.inventorySlotCount step this.inventory.size.rows) {
                    val currentPosition = MathUtils.slotToPosition(currentSlot, this.inventory.size.columns)
                    setItem(currentPosition, item)
                }
            }

            RIGHT_BORDER -> {
                val rowSize = this.inventory.size.rows
                val lastColumnStart = rowSize - 1
                val lastColumnEnd = this.inventorySlotCount - 1

                for (currentSlot in lastColumnStart..lastColumnEnd step rowSize) {
                    val currentPos = MathUtils.slotToPosition(currentSlot, this.inventory.size.columns)
                    val nextPos = MathUtils.nextPositionFromSlot(currentSlot, this.inventory.size.columns)

                    if (currentPos.row == nextPos.row) continue

                    val currentPosition = MathUtils.slotToPosition(currentSlot, this.inventory.size.columns)
                    setItem(currentPosition, item)
                }
            }

            TOP_BORDER -> {
                for (currentSlot in 0 until this.inventory.size.columns) {
                    val currentPosition = MathUtils.slotToPosition(currentSlot, this.inventory.size.columns)
                    setItem(currentPosition, item)
                }
            }

            BOTTOM_BORDER -> {
                val size = this.inventorySlotCount
                val firstColumnInLastRow = size - this.inventory.size.columns

                for (currentSlot in firstColumnInLastRow until size) {
                    val currentPosition = MathUtils.slotToPosition(currentSlot, this.inventory.size.columns)
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
        getRawInventory().clear(MathUtils.positionToSlot(pos, this.inventory.size.columns))
    }

    override fun isPositionTaken(pos: InventoryPosition): Boolean {
        return this.contents[pos] != null
    }

    override fun getPositionOfItem(item: InteractiveItem): InventoryPosition? {
        var result: InventoryPosition? = null
        for (entry in this.contents.entries) if (entry.value != null && entry.value == item) result = entry.key
        return result
    }

    override fun getFirstEmptySlot(): InventoryPosition? {
        var emptyPosition: InventoryPosition? = null

        for (position in this.contents.keys) if (this.contents[position] == null) {
            emptyPosition = position
            break
        }

        return emptyPosition
    }

    override fun getItem(position: InventoryPosition): InteractiveItem? {
        return this.contents[position]
    }

    override fun getItem(row: Int, column: Int): InteractiveItem? {
        return this.contents[InventoryPosition.of(row, column)]
    }

    override fun findFirstItemWithType(material: Material): Pair<InventoryPosition, InteractiveItem>? {
        var result: Pair<InventoryPosition, InteractiveItem>? = null

        for (slot in 0..this.inventorySlotCount) {
            val currentPosition = MathUtils.slotToPosition(slot, this.inventory.size.columns)
            if (this.contents[currentPosition] == null) continue
            if (this.contents[currentPosition]?.item == null) continue
            if (this.contents[currentPosition] != null && this.contents[currentPosition]!!.item.type == material)
                result = Pair(currentPosition, this.contents[currentPosition]!!)
        }

        return result
    }

    override fun createPagination(): InventoryPagination {
        if (this.pagination == null) this.pagination = InventoryPaginationImpl(this)
        return this.pagination!!
    }

    override fun getRawInventory(): Inventory {
        return this.inventory.rawInventory
    }
}