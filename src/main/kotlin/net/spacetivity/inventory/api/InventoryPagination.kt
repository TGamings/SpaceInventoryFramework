package net.spacetivity.inventory.api

import com.google.common.collect.ArrayListMultimap
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition

interface InventoryPagination {

    val pageSlots: MutableList<InventoryPosition>
    val pageItems: ArrayListMultimap<Int, InteractiveItem>

    fun getCurrentPageId(): Int
    fun getLastPageId(): Int
    fun getPageAmount(): Int

    fun isFirstPage(): Boolean
    fun isLastPage(): Boolean

    fun toFirstPage()
    fun toLastPage()

    fun toNextPage()
    fun toPreviousPage()

    fun setPageSlotsFrom(startRow: Int, startColumn: Int)
    fun distributeItems(items: MutableList<InteractiveItem>)
    fun limitItemsPerPage(amount: Int)
    fun refreshPage()

}