package net.spacetivity.inventory.logic

import com.google.common.collect.ArrayListMultimap
import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.api.InventoryPagination
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition

class InventoryPaginationImpl(private val controller: InventoryController) : InventoryPagination {

    override val pageSlots: MutableList<InventoryPosition> = mutableListOf()
    override val pageItems: ArrayListMultimap<Int, InteractiveItem> = ArrayListMultimap.create()
    private var itemsPerPage = 9
    private var index = 0

    override fun getCurrentPageId(): Int {
        return this.index
    }

    override fun getLastPageId(): Int {
        val pageIds: List<Int> = this.pageItems.keys().toList()
        return pageIds[pageIds.size - 1]
    }

    override fun getPageAmount(): Int {
        return this.pageItems.keys().toList().size
    }

    override fun isFirstPage(): Boolean {
        return this.index == 0
    }

    override fun isLastPage(): Boolean {
        val pageIds: List<Int> = this.pageItems.keys().toList()
        return this.index == pageIds[pageIds.size - 1]
    }

    override fun toFirstPage() {
        this.index = 0
        refreshPage()
    }

    override fun toLastPage() {
        val pageIds: List<Int> = this.pageItems.keys().toList()
        val lastPageId = pageIds[pageIds.size - 1]
        this.index = lastPageId
        refreshPage()
    }

    override fun toNextPage() {
        this.index = if (isLastPage()) 0 else this.index + 1
        refreshPage()
    }

    override fun toPreviousPage() {
        if (isFirstPage()) return
        this.index -= 1
        refreshPage()
    }

    override fun setPageSlotsFrom(startRow: Int, startColumn: Int) {
        for (row in startRow until this.controller.inventory.size.rows) {
            for (column in startColumn until this.controller.inventory.size.columns) {
                val position = InventoryPosition.of(row, column)
                this.pageSlots.add(position)
            }
        }
    }

    override fun distributeItems(items: MutableList<InteractiveItem>) {
        this.pageItems.clear()
        var pageIndex = 0

        for (i in items.indices) {
            if (i % itemsPerPage == 0) pageIndex = i / itemsPerPage
            this.pageItems.put(pageIndex, items[i])
        }

        refreshPage()
    }

    override fun limitItemsPerPage(amount: Int) {
        this.itemsPerPage = amount
    }

    override fun refreshPage() {
        for (currentPosition in this.pageSlots) {
            if (!this.controller.isPositionTaken(currentPosition)) continue
            this.controller.clearSlot(currentPosition)
        }

        val itemsForNextPage = this.pageItems.get(this.index)
        var itemIndex = 0

        for (currentPosition in this.pageSlots) {
            if (itemIndex >= itemsForNextPage.size) break
            if (this.controller.isPositionTaken(currentPosition)) continue

            val currentItem = itemsForNextPage[itemIndex]

            this.controller.setItem(currentPosition, currentItem)
            itemIndex++
        }

        this.controller.inventory.updateRawInventory()
    }
}