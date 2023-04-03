package net.spacetivity.inventory.item

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.api.InventoryPagination
import net.spacetivity.inventory.item.InteractiveItem.Modification.*
import net.spacetivity.inventory.utils.MathUtils
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

data class InteractiveItem(var item: ItemStack, val action: (InventoryPosition, InventoryClickEvent) -> Unit) {

    fun runAction(position: InventoryPosition, event: InventoryClickEvent) = this.action.invoke(position, event)

    fun update(controller: InventoryController, modification: Modification, vararg values: Any) {
        if (values.size > 1)
            throw UnsupportedOperationException("There are no more then one values allowed! Current size: ${values.size}")

        val newValue = values[0]
        val tempItemMeta = this.item.itemMeta

        when (modification) {
            DISPLAY_NAME -> {
                if (newValue !is String)
                    throw UnsupportedOperationException("'newValue' is not an String!")

                tempItemMeta.displayName(Component.text(newValue))
            }

            LORE -> {
                if (!(newValue is Array<*> && newValue.isArrayOf<String>()))
                    throw UnsupportedOperationException("'newValue' is not an String Array!")

                @Suppress("UNCHECKED_CAST") val raw: Array<String> = newValue as Array<String>
                val lore = raw.map { line -> Component.text(line) }.toList()

                tempItemMeta.lore(lore)
            }

            AMOUNT -> {
                if (newValue !is Int)
                    throw UnsupportedOperationException("'newValue' is not an Int!")

                this.item.amount = newValue
            }

            INCREMENT -> {
                this.item.amount += newValue as Int
            }

            ENCHANTMENTS -> {
                if (newValue !is ItemEnchantment)
                    throw UnsupportedOperationException("'newValue' is not an ItemEnchantment!")

                newValue.performAction(this.item)
            }
        }

        this.item.itemMeta = tempItemMeta

        val positionOfItem = controller.getPositionOfItem(this)!!
        val rawInventory = controller.getRawInventory()

        rawInventory.remove(this.item.type)
        rawInventory.setItem(MathUtils.positionToSlot(positionOfItem, controller.inventory.size.columns), this.item)
    }

    enum class Modification {
        DISPLAY_NAME,
        LORE,
        AMOUNT,
        INCREMENT,
        ENCHANTMENTS
    }

    companion object {

        fun placeholder(material: Material): InteractiveItem {
            return of(makeItemStack(material))
        }

        fun nextPage(item: ItemStack, pagination: InventoryPagination): InteractiveItem {
            return of(item) { _, _ -> pagination.toNextPage() }
        }

        fun previousPage(item: ItemStack, pagination: InventoryPagination): InteractiveItem {
            return of(item) { _, _ -> pagination.toPreviousPage() }
        }

        fun of(item: ItemStack): InteractiveItem {
            return InteractiveItem(item) { _, _ -> }
        }

        fun of(item: ItemStack, action: (InventoryPosition, InventoryClickEvent) -> Unit): InteractiveItem {
            return InteractiveItem(item, action)
        }

        private fun makeItemStack(material: Material): ItemStack {
            val itemStack = ItemStack(material)
            val itemMeta = itemStack.itemMeta
            itemMeta.displayName(Component.text(" "))
            itemStack.itemMeta = itemMeta
            return itemStack
        }

    }

}